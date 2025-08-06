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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileSetupFragment : Fragment(R.layout.fragment_profile_setup) {

    private lateinit var backIcon: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var nicknameGuide1: TextView
    private lateinit var nicknameGuide2: TextView
    private lateinit var nextButton: AppCompatButton

    // [테스트용] 임시 중복 닉네임 리스트
    // TODO : API 연동하기
    private val takenNicknames = listOf("planup")

    private var cameraImageUri: Uri? = null

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        editIcon = view.findViewById(R.id.editIcon)
        nicknameEditText = view.findViewById(R.id.nicknameEditText)
        nicknameGuide1 = view.findViewById(R.id.nicknameGuide1)
        nicknameGuide2 = view.findViewById(R.id.nicknameGuide2)
        nextButton = view.findViewById(R.id.nextButton)

        /* 처음엔 안내문 숨김 */
        nicknameGuide1.visibility = View.GONE
        nicknameGuide2.visibility = View.GONE

        /* 처음에는 다음 버튼 비활성화 */
        setNextButtonEnabled(false)

        /* 닉네임 입력 변화 감지 → 유효성 검사 */
        nicknameEditText.addTextChangedListener {
            val nickname = it.toString().trim()

            nicknameGuide1.visibility = View.GONE
            nicknameGuide2.visibility = View.GONE

            val isTooLong = nickname.length > 20
            val isTaken = takenNicknames.contains(nickname)
            val isEmpty = nickname.isEmpty()

            when {
                isTooLong -> {
                    // 20자 초과 → 안내문 표시
                    nicknameGuide1.visibility = View.VISIBLE
                    setNextButtonEnabled(false)
                }

                isTaken -> {
                    // 중복 닉네임 → 안내문 표시
                    nicknameGuide2.visibility = View.VISIBLE
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
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(LoginSentEmailFragment())
        }

        /* editIcon 클릭 → 프로필 수정 popup 띄우기 */
        editIcon.setOnClickListener {
            showProfilePopup(it)
        }

        /* 다음 버튼 클릭 → InviteCodeFragment로 이동 */
        nextButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            val isTooLong = nickname.length > 20
            val isTaken = takenNicknames.contains(nickname)
            val isEmpty = nickname.isEmpty()

            // 조건 불만족
            if (isEmpty || isTooLong || isTaken) {
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
                uploadProfileImage(uri)
            }
        }

        // 카메라
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && cameraImageUri != null) {
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
                    uploadProfileImage(uri)
                } else {
                    Toast.makeText(requireContext(), "jpg 또는 png만 업로드할 수 있어요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (nicknameEditText.isFocused) {
                    nicknameEditText.clearFocus()
                    hideKeyboard()
                }
                view.performClick()
            }
            false
        }
    }

    /* 다음 버튼 활성 ↔ 비활성 처리 함수 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        nextButton.isEnabled = enabled
        // 버튼 상태에 따라 selector 적용됨
        nextButton.setBackgroundResource(R.drawable.btn_next_background)
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
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_profile, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        val galleryOption = popupView.findViewById<LinearLayout>(R.id.selectFromGallery)
        val cameraOption = popupView.findViewById<LinearLayout>(R.id.takePhoto)
        val fileOption = popupView.findViewById<LinearLayout>(R.id.selectFile)

        galleryOption.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
            popupWindow.dismiss()
        }

        cameraOption.setOnClickListener {
            val photoFile = createImageFile()
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            cameraLauncher.launch(intent)
            popupWindow.dismiss()
        }

        fileOption.setOnClickListener {
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
        val file = File(getRealPathFromURI(uri))
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
                    }
                } else {
                    Log.e("프로필 업로드", "실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("프로필 업로드", "예외 발생: ${e.message}")
            }
        }
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
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
