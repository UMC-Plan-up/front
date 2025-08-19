package com.example.planup.signup.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.databinding.FragmentProfileSetupBinding
import com.example.planup.databinding.PopupProfileBinding
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.bumptech.glide.Glide
import com.example.planup.network.App
import com.example.planup.signup.data.*

class ProfileSetupFragment : Fragment() {

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    private var cameraImageUri: Uri? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileLauncher: ActivityResultLauncher<Intent>
    private var latestPhotoFile: File? = null
    private var tempUserId: String? = null
    private var nicknameCheckJob: Job? = null
    private var isNicknameAvailable: Boolean = false
    private var verifyToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 딥링크에서 전달된 토큰/이메일 받기
        verifyToken = arguments?.getString("verifyToken")
        Log.d("ProfileSetup", "verifyToken arg = ${verifyToken?.take(12)}...")
        val emailFromDeepLink = arguments?.getString("email")

        // 2) 토큰 적용
        verifyToken?.takeIf { it.isNotBlank() }?.let {
            App.jwt.token = "Bearer $it"
            Log.d("ProfileSetup", "Applied verify token for signup flow")
        }

        (requireActivity() as SignupActivity).email =
            (requireActivity() as SignupActivity).email ?: emailFromDeepLink

        tempUserId = arguments?.getString("tempUserId")
        val isKakaoSignup = !tempUserId.isNullOrBlank()

        binding.nicknameGuide1.visibility = View.GONE
        binding.nicknameGuide2.visibility = View.GONE
        setNextButtonEnabled(false)

        /* 닉네임 입력 변화 감지 → 유효성 검사 */
        binding.nicknameEditText.addTextChangedListener {
            val nickname = it.toString().trim()

            binding.nicknameGuide1.visibility = View.GONE
            binding.nicknameGuide2.visibility = View.GONE

            val isTooLong = nickname.length > 20
            val isEmpty = nickname.isEmpty()

            when {
                isTooLong -> {
                    // 20자 초과 → 안내문 표시
                    binding.nicknameGuide1.visibility = View.VISIBLE
                    isNicknameAvailable = false
                    setNextButtonEnabled(false)
                    nicknameCheckJob?.cancel()
                }

                isEmpty -> {
                    isNicknameAvailable = false
                    setNextButtonEnabled(false)
                    nicknameCheckJob?.cancel()
                }

                else -> {
                    // 모든 조건 만족 → 버튼 활성화
                    isNicknameAvailable = false
                    setNextButtonEnabled(false)
                    nicknameCheckJob?.cancel()
                    nicknameCheckJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(350)
                        checkNicknameDuplicate(nickname)
                    }
                }
            }
            Log.d("PSF", "args email=$emailFromDeepLink, verifyToken=${verifyToken?.take(12)}..., jwt=${App.jwt.token}")
        }

        // 뒤로가기 아이콘 → 이전 화면으로 이동
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )

        /* editIcon 클릭 → 프로필 수정 popup 띄우기 */
        binding.editIcon.setOnClickListener {
            showProfilePopup(it)
        }

        /* 다음 버튼 클릭 → 최종 회원가입 진행 */
        binding.nextButton.setOnClickListener {
            val nickname = binding.nicknameEditText.text.toString().trim()
            val isTooLong = nickname.length > 20
            val isEmpty = nickname.isEmpty()

            if (isEmpty || isTooLong) {
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                if (!isNicknameAvailable) {
                    setNextButtonEnabled(false)
                    checkNicknameDuplicate(nickname)
                }
                if (!isNicknameAvailable) return@launch

                saveProfileData(nickname)

                completeSignup()
            }
        }

        // 갤러리
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                Glide.with(this).load(uri).circleCrop().into(binding.profileImage)
                uploadProfileImage(uri)
            }
        }

        // 카메라
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && cameraImageUri != null) {
                Glide.with(this).load(cameraImageUri).circleCrop().into(binding.profileImage)
                uploadProfileImage(cameraImageUri!!)
            }
        }

        // 파일
        fileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val fileName = getFileNameFromUri(uri)
                val extension = fileName?.substringAfterLast('.', "")?.lowercase()
                if (extension == "jpg" || extension == "jpeg" || extension == "png") {
                    Glide.with(this).load(uri).circleCrop().into(binding.profileImage)
                    uploadProfileImage(uri)
                } else {
                    Toast.makeText(requireContext(), "jpg 또는 png만 업로드할 수 있어요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.nicknameEditText.isFocused) {
                    binding.nicknameEditText.clearFocus()
                    hideKeyboard()
                }
                view.performClick()
            }
            false
        }

        if (isKakaoSignup) {
            (requireActivity() as SignupActivity).profileImgUrl?.let { url ->
                if (url.isNotBlank()) {
                    Glide.with(this).load(url).circleCrop().into(binding.profileImage)
                }
            }
        }

        fetchRandomNickname()
    }

    /* 최종 회원가입 API 호출을 담당하는 함수 */
    private fun completeSignup() {
        val activity = requireActivity() as SignupActivity
        val isKakaoSignup = !activity.tempUserId.isNullOrBlank()

        lifecycleScope.launch {
            try {
                if (isKakaoSignup) {
                    val tempUserId = activity.tempUserId ?: return@launch
                    val request = KakaoCompleteRequest(
                        tempUserId = tempUserId,
                        nickname = activity.nickname ?: "",
                        profileImg = activity.profileImgUrl,
                        agreements = activity.agreements?.map {
                            KakaoCompleteRequest.Agreement(it.termsId, it.isAgreed)
                        } ?: emptyList()
                    )

                    Log.d("회원가입/카카오", "request=$request")
                    Log.d("회원가입/카카오", "Authorization(App.jwt)=${App.jwt.token}")

                    val response = RetrofitInstance.userApi.kakaoComplete(request)
                    val code = response.code()
                    val ok = response.isSuccessful
                    val body = response.body()
                    val err = response.errorBody()?.string()

                    Log.d("회원가입/카카오", "HTTP code=$code isSuccessful=$ok")
                    Log.d("회원가입/카카오", "body?.isSuccess=${body?.isSuccess} message=${body?.message}")
                    if (!err.isNullOrEmpty()) Log.e("회원가입/카카오", "errorBody=$err")

                    if (ok && body?.isSuccess == true) {
                        val result = body.result
                        val accessToken = result.accessToken
                        Log.d("회원가입/카카오", "OK accessToken.len=${accessToken?.length}")

                        if (accessToken != null) {
                            val prefs = requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("accessToken", accessToken)
                                putLong("userId", result.id.toLong())
                                putString("email", result.email)
                                putString("nickname", result.userInfo?.nickname ?: activity.nickname ?: "")
                                putString("profileImg", result.userInfo?.profileImg ?: activity.profileImgUrl ?: "")
                            }.apply()

                            App.jwt.token = "Bearer $accessToken"
                            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
                        }
                    } else {
                        Toast.makeText(requireContext(), body?.message ?: "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val restoredEmail = activity.email ?: SignUpDraftStore.loadEmail(requireContext())
                    val restoredPw = activity.password ?: SignUpDraftStore.loadPw(requireContext())

                    Log.d("회원가입", "restoredEmail=$restoredEmail pw.len=${restoredPw?.length} nickname=${activity.nickname}")
                    Log.d("회원가입", "agreements=${activity.agreements}")
                    Log.d("회원가입", "profileImg='${activity.profileImgUrl}'")
                    Log.d("회원가입", "Authorization(App.jwt)=${App.jwt.token}")

                    if (restoredEmail.isNullOrBlank()) {
                        Log.e("회원가입", "email 복원 실패 → LoginEmailFragment")
                        (requireActivity() as SignupActivity).navigateToFragment(LoginEmailFragment()); return@launch
                    }
                    if (restoredPw.isNullOrBlank()) {
                        Log.e("회원가입", "pw 복원 실패 → LoginPasswordFragment")
                        (requireActivity() as SignupActivity).navigateToFragment(LoginPasswordFragment()); return@launch
                    }

                    val request = SignupRequestDto(
                        email = restoredEmail,
                        password = restoredPw,
                        passwordCheck = restoredPw,
                        nickname = activity.nickname ?: "",
                        profileImg = activity.profileImgUrl ?: "",
                        agreements = activity.agreements?.map { Agreement(it.termsId, it.isAgreed) } ?: emptyList()
                    )
                    Log.d("회원가입", "request=$request")

                    val response = RetrofitInstance.userApi.signup(request)
                    val code = response.code()
                    val ok = response.isSuccessful
                    val body = response.body()
                    val err = response.errorBody()?.string()

                    Log.d("회원가입", "HTTP code=$code isSuccessful=$ok")
                    Log.d("회원가입", "body?.isSuccess=${body?.isSuccess} message=${body?.message}")
                    if (!err.isNullOrEmpty()) Log.e("회원가입", "errorBody=$err")

                    if (ok && body?.isSuccess == true) {
                        val result = body.result
                        val accessToken = result?.accessToken
                        Log.d("회원가입", "OK accessToken.len=${accessToken?.length}")

                        if (accessToken != null) {
                            val prefs = requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("accessToken", accessToken)
                                putLong("userId", result.id.toLong())
                                putString("email", result.email)
                                putString("nickname", result.userInfo?.nickname ?: activity.nickname ?: "")
                                putString("profileImg", result.userInfo?.profileImg ?: activity.profileImgUrl ?: "")
                            }.apply()

                            App.jwt.token = "Bearer $accessToken"
                            SignUpDraftStore.clear(requireContext())
                            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
                        }
                    } else {
                        Toast.makeText(requireContext(), body?.message ?: "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("회원가입", "네트워크 예외", e)
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* 다음 버튼 활성 ↔ 비활성 처리 함수 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
        binding.nextButton.setBackgroundResource(R.drawable.btn_next_background)
    }

    /* 현재 닉네임과 프로필 이미지를 저장하는 함수 */
    private fun saveProfileData(nickname: String) {
        val activity = requireActivity() as SignupActivity
        activity.nickname = nickname
    }

    /* 사진 저장할 파일 만드는 함수 */
    private fun createImageFile(): File {
        val fileName = "profile_${System.currentTimeMillis()}"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    /* editIcon 클릭 시 popup_profile.xml 띄우는 함수 */
    private fun showProfilePopup(anchorView: View) {
        val popupBinding = PopupProfileBinding.inflate(LayoutInflater.from(requireContext()))

        val popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        popupBinding.selectFromGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
            popupWindow.dismiss()
        }

        popupBinding.takePhoto.setOnClickListener {
            val photoFile = createImageFile()
            latestPhotoFile = photoFile
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            cameraLauncher.launch(intent)
            popupWindow.dismiss()
        }

        popupBinding.selectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            fileLauncher.launch(Intent.createChooser(intent, "파일 선택"))
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(
            anchorView,
            -anchorView.width / 2,
            0,
            Gravity.END
        )
    }

    /* 프로필 이미지 업로드 → 서버에 POST */
    private fun uploadProfileImage(uri: Uri) {
        val file = when {
            cameraImageUri != null && uri == cameraImageUri && latestPhotoFile != null -> latestPhotoFile!!
            uri.scheme.equals("content", ignoreCase = true) -> copyUriToCache(uri)
            uri.scheme.equals("file", ignoreCase = true) -> File(uri.path!!)
            else -> copyUriToCache(uri)
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("프로필 업로드", "파일명: ${file.name}, 크기: ${file.length()} bytes")
                Log.d("프로필 업로드", "JWT 토큰: ${App.jwt.token}")

                val response = RetrofitInstance.profileApi.uploadProfileImage(App.jwt.token ?: "", body)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.result?.imageUrl
                    Log.d("프로필 업로드", "성공! URL: $imageUrl")

                    withContext(Dispatchers.Main) {
                        (requireActivity() as SignupActivity).profileImgUrl = imageUrl ?: ""
                        if (!imageUrl.isNullOrBlank()) {
                            Glide.with(this@ProfileSetupFragment)
                                .load(imageUrl)
                                .circleCrop()
                                .into(binding.profileImage)
                        }
                    }
                } else {
                    Log.e("프로필 업로드", "실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("프로필 업로드", "예외 발생", e)
            }
        }
    }

    private fun copyUriToCache(uri: Uri): File {
        val resolver = requireContext().contentResolver

        var name = "upload_${System.currentTimeMillis()}.jpg"
        resolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (idx >= 0) name = c.getString(idx)
            }
        }

        val outFile = File(requireContext().cacheDir, name)
        resolver.openInputStream(uri)?.use { input ->
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return outFile
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            val fileName = cursor.getString(nameIndex)
            cursor.close()
            fileName
        } else {
            uri.lastPathSegment
        }
    }

    /* 키보드 숨기는 메서드 */
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /* 랜덤 닉네임 생성 */
    private fun fetchRandomNickname() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitInstance.profileApi.getRandomNickname()
                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    val nn = res.body()!!.result.nickname
                    binding.nicknameEditText.setText(nn)
                } else {
                    val msg = res.body()?.message ?: "닉네임 생성 실패"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun checkNicknameDuplicate(nickname: String) {
        try {
            val res = RetrofitInstance.userApi.checkNickname(nickname)
            if (res.isSuccessful && res.body()?.isSuccess == true) {
                val available = res.body()!!.result?.available == true
                isNicknameAvailable = available
                if (available) {
                    binding.nicknameGuide2.visibility = View.GONE
                    setNextButtonEnabled(true)
                } else {
                    binding.nicknameGuide2.visibility = View.VISIBLE
                    setNextButtonEnabled(false)
                }
            } else {
                isNicknameAvailable = false
                binding.nicknameGuide2.visibility = View.VISIBLE
                setNextButtonEnabled(false)
            }
        } catch (e: Exception) {
            isNicknameAvailable = false
            binding.nicknameGuide2.visibility = View.VISIBLE
            setNextButtonEnabled(false)
        }
    }
}
