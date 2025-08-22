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

        verifyToken = arguments?.getString("verifyToken")
        Log.d("ProfileSetup", "verifyToken arg = ${verifyToken?.take(12)}...")
        val emailFromDeepLink = arguments?.getString("email")

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

        binding.nicknameEditText.addTextChangedListener {
            val nickname = it.toString().trim()

            binding.nicknameGuide1.visibility = View.GONE
            binding.nicknameGuide2.visibility = View.GONE

            val isTooLong = nickname.length > 20
            val isEmpty = nickname.isEmpty()

            when {
                isTooLong -> {
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

        binding.editIcon.setOnClickListener {
            showProfilePopup(it)
        }

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

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                Glide.with(this).load(uri).circleCrop().into(binding.profileImage)
                uploadProfileImage(uri)
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && cameraImageUri != null) {
                Glide.with(this).load(cameraImageUri).circleCrop().into(binding.profileImage)
                uploadProfileImage(cameraImageUri!!)
            }
        }

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

                    val response = RetrofitInstance.userApi.kakaoComplete(request)
                    val ok = response.isSuccessful
                    val body = response.body()
                    val err = response.errorBody()?.string()

                    if (ok && body?.isSuccess == true) {
                        val result = body.result
                        val accessToken = result.accessToken

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

                    val request = SignupRequestDto(
                        email = restoredEmail ?: "",
                        password = restoredPw ?: "",
                        passwordCheck = restoredPw ?: "",
                        nickname = activity.nickname ?: "",
                        profileImg = activity.profileImgUrl ?: "",
                        agreements = activity.agreements?.map { Agreement(it.termsId, it.isAgreed) } ?: emptyList()
                    )

                    val response = RetrofitInstance.userApi.signup(request)
                    val ok = response.isSuccessful
                    val body = response.body()
                    val err = response.errorBody()?.string()

                    if (ok && body?.isSuccess == true) {
                        val result = body.result
                        val accessToken = result?.accessToken

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
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
        binding.nextButton.setBackgroundResource(R.drawable.btn_next_background)
    }

    private fun saveProfileData(nickname: String) {
        val activity = requireActivity() as SignupActivity
        activity.nickname = nickname
    }

    private fun createImageFile(): File {
        val fileName = "profile_${System.currentTimeMillis()}"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

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

    private fun uploadProfileImage(uri: Uri) {
        val file = when {
            cameraImageUri != null && uri == cameraImageUri && latestPhotoFile != null -> latestPhotoFile!!
            uri.scheme.equals("content", ignoreCase = true) -> copyUriToCache(uri)
            uri.scheme.equals("file", ignoreCase = true) -> File(uri.path!!)
            else -> copyUriToCache(uri)
        }

        val mime = requireContext().contentResolver.getType(uri) ?: "image/jpeg"
        val requestFile = file.asRequestBody(mime.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = App.jwt.token ?: run {
                    Log.e("프로필 업로드", "토큰이 null입니다. API 요청을 중단합니다.")
                    return@launch
                }

                val email = (requireActivity() as SignupActivity).email
                    ?: SignUpDraftStore.loadEmail(requireContext())
                    ?: ""

                Log.d("UploadProfile", "API 요청 직전 이메일: '$email'")
                if (email.isBlank()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "이메일 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                Log.d("프로필 업로드", "API 요청 시작. 토큰: ${token.take(10)}..., email=$email")

                val response = RetrofitInstance.profileApi.uploadProfileImage(email, body)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.result?.imageUrl
                    Log.d("프로필 업로드", "API 요청 성공. 받은 이미지 URL: $imageUrl")

                    withContext(Dispatchers.Main) {
                        (requireActivity() as SignupActivity).profileImgUrl = imageUrl ?: ""

                        Log.d("프로필 업로드", "SignupActivity의 profileImgUrl 변수 업데이트: ${(requireActivity() as SignupActivity).profileImgUrl}")

                        if (!imageUrl.isNullOrBlank()) {
                            Log.d("프로필 업로드", "Glide로 이미지 로딩 시작: $imageUrl")
                            Glide.with(this@ProfileSetupFragment)
                                .load(imageUrl)
                                .circleCrop()
                                .into(binding.profileImage)
                        } else {
                            Log.w("프로필 업로드", "서버에서 받은 이미지 URL이 비어 있습니다. Glide 로딩을 건너뜁니다.")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorCode = response.code()
                    Log.e("프로필 업로드", "API 요청 실패. 상태 코드: $errorCode, 오류 본문: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("프로필 업로드", "예외 발생: ${e.message}", e)
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchRandomNickname() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitInstance.profileApi.getRandomNickname()
                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    val nn = res.body()!!.result.nickname
                    binding.nicknameEditText.setText(nn)
                }
            } catch (_: Exception) { }
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
