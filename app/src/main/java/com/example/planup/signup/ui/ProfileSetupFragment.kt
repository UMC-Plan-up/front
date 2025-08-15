package com.example.planup.signup.ui

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
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

class ProfileSetupFragment : Fragment() {

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!

    private var cameraImageUri: Uri? = null

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileLauncher: ActivityResultLauncher<Intent>
    private var latestPhotoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    setNextButtonEnabled(false)
                }

                isEmpty -> {
                    setNextButtonEnabled(false)
                }

                else -> {
                    // 모든 조건 만족 → 버튼 활성화
                    setNextButtonEnabled(true)
                }
            }
        }

        // 뒤로가기 아이콘 → 이전 화면으로 이동
        binding.backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(LoginSentEmailFragment())
        }

        /* editIcon 클릭 → 프로필 수정 popup 띄우기 */
        binding.editIcon.setOnClickListener {
            showProfilePopup(it)
        }

        /* 다음 버튼 클릭 → InviteCodeFragment로 이동 */
        binding.nextButton.setOnClickListener {
            val nickname = binding.nicknameEditText.text.toString().trim()
            val isTooLong = nickname.length > 20
            val isEmpty = nickname.isEmpty()

            if (isEmpty || isTooLong) {
                return@setOnClickListener
            }

            // 조건 만족
            saveProfileData(nickname)
            openNextStep()
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
        fetchRandomNickname()
    }

    /* 다음 버튼 활성 ↔ 비활성 처리 함수 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
        // 버튼 상태에 따라 selector 적용됨
        binding.nextButton.setBackgroundResource(R.drawable.btn_next_background)
    }

    /* InviteCodeFragment로 이동하는 메서드 */
    private fun openNextStep() {
        // SignupActivity의 navigateToFragment() 호출
        (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
    }

    /* 현재 닉네임과 프로필 이미지를 저장하는 함수 */
    private fun saveProfileData(nickname: String) {
        val activity = requireActivity() as SignupActivity
        activity.nickname = nickname
        // 이미지 업로드 성공 시 저장된 URL이 activity.profileImgUrl에 들어감
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
            // 카메라 결과: 우리가 직접 만든 파일이 있음
            cameraImageUri != null && uri == cameraImageUri && latestPhotoFile != null -> latestPhotoFile!!

            // content:// URI → 캐시로 복사
            uri.scheme.equals("content", ignoreCase = true) -> copyUriToCache(uri)

            // file:// URI → 바로 파일
            uri.scheme.equals("file", ignoreCase = true) -> File(uri.path!!)

            else -> copyUriToCache(uri)
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.profileApi.uploadProfileImage(body)
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
                Log.e("프로필 업로드", "예외 발생: ${e.message}")
            }
        }
    }

    private fun copyUriToCache(uri: Uri): File {
        val resolver = requireContext().contentResolver

        // 파일명 얻기 (없으면 타임스탬프 사용)
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

    /* URI -> 실제 파일 경로로 변환 */
    private fun getRealPathFromURI(uri: Uri): String {
        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndex(MediaStore.Images.Media.DATA) ?: return ""
        val path = cursor.getString(index)
        cursor.close()
        return path
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
}
