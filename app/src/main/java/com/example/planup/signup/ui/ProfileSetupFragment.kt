package com.example.planup.signup.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.SignupActivity

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


        nextButton.setBackgroundColor(
            requireContext().getColor(R.color.blue_200)
        )  // 버튼 항상 활성화

        /* 닉네임 입력 변화 감지 → 유효성 검사 */
        nicknameEditText.addTextChangedListener {
            val nickname = it.toString().trim()

            nicknameGuide1.visibility = View.GONE
            nicknameGuide2.visibility = View.GONE

            val isTooLong = nickname.length > 20
            val isTaken = takenNicknames.contains(nickname)

            when {
                isTooLong -> {
                    // 20자 초과 → 안내문 표시
                    nicknameGuide1.visibility = View.VISIBLE
                }

                isTaken -> {
                    // 중복 닉네임 → 안내문 표시
                    nicknameGuide2.visibility = View.VISIBLE
                }

                else -> {
                    // 조건 만족 시 안내문 없음
                }
            }
        }

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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
    }


    /* InviteCodeFragment로 이동하는 메서드 */
    private fun openNextStep() {
        // SignupActivity의 navigateToFragment() 호출
        (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
    }


    /* 현재 닉네임과 프로필 이미지를 저장하는 함수 (나중에 API 연동 시 사용) */
    private fun saveProfileData(nickname: String) {
        val selectedNickname = nickname  // 현재 닉네임
        val profileImageDrawable = editIcon.drawable  // 현재 프로필 이미지
        // TODO: 닉네임과 프로필을 회원가입 API요청 시 서버에 함께 전송해야 함
    }


    /*  editIcon 클릭 시 popup_profile.xml 띄우는 함수 */
    private fun showProfilePopup(anchorView: View) {
        // popup_profile.xml inflate
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_profile, null)

        // PopupWindow 생성
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // popup 바깥 클릭 시 닫기
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        // 팝업 내부의 클릭 이벤트 연결
        val galleryOption = popupView.findViewById<LinearLayout>(R.id.selectFromGallery)
        val cameraOption = popupView.findViewById<LinearLayout>(R.id.takePhoto)
        val fileOption = popupView.findViewById<LinearLayout>(R.id.selectFile)

        galleryOption.setOnClickListener {
            // TODO: 사진 보관함 열기
            popupWindow.dismiss()
        }
        cameraOption.setOnClickListener {
            // TODO: 사진 찍기 실행
            popupWindow.dismiss()
        }
        fileOption.setOnClickListener {
            // TODO: 파일 선택 실행
            popupWindow.dismiss()
        }

        // popup 띄우기
        popupWindow.showAsDropDown(
            anchorView,
            -anchorView.width / 2,  // 살짝 왼쪽 정렬
            0,
            Gravity.END           // 오른쪽 기준 정렬
        )
    }
}

