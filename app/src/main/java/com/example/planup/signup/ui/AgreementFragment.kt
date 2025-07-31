package com.example.planup.signup.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.login.LoginActivity

class AgreementFragment : Fragment() {

    private lateinit var checkAge: CheckBox
    private lateinit var checkTerms: CheckBox
    private lateinit var checkMarketing: CheckBox
    private lateinit var checkAd: CheckBox
    private lateinit var checkAll: CheckBox
    private lateinit var nextButton: Button
    private lateinit var progressBar: ProgressBar

    private var isRequiredChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agreement, container, false)

        val errorBox = view.findViewById<TextView>(R.id.requiredErrorText)
        errorBox.visibility = View.GONE

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // 현재 회원가입 플로우 종료
        }


        progressBar = view.findViewById(R.id.progressBar)

        // 체크박스 초기화
        checkAll = view.findViewById(R.id.checkAll)
        checkAge = view.findViewById(R.id.checkAge)
        checkTerms = view.findViewById(R.id.checkTerms)
        checkMarketing = view.findViewById(R.id.checkMarketing)
        checkAd = view.findViewById(R.id.checkAd)
        nextButton = view.findViewById(R.id.nextButton)


        setupCheckAllFeature()  // 전체동의 ↔ 개별 체크박스 연동


        setupRequiredCheckFeature()  // [필수] 체크박스 상태 감지

        // "자세히" detail1 → popup 띄우기
        // TODO: 약관 팝업창 로직 연결
        val detail1 = view.findViewById<TextView>(R.id.detail1)
        detail1.setOnClickListener {
            showTermsPopup()
        }


        /* 다음 버튼 클릭 → LoginEmailFragment로 이동 */
        nextButton.setOnClickListener {
            if (isRequiredChecked) {
                // [필수] 모두 선택됨 → LoginEmailFragment로 이동
                openNextStep()
            } else {
                // [필수] 미선택 → 에러 박스 표시
                showRequiredError(view)
            }
        }
        return view
    }


    /* LoginEmailFragment로 이동하는 메서드 */
    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginEmailFragment()) // 다음 단계로 이동
            .addToBackStack(null) // 뒤로가기 가능
            .commit()
    }


    private fun setupCheckAllFeature() {
        val individualChecks = listOf(checkAge, checkTerms, checkMarketing, checkAd)

        // 전체동의 → 나머지 전부 체크/해제
        checkAll.setOnCheckedChangeListener { _, isChecked ->
            individualChecks.forEach { it.isChecked = isChecked }
        }

        // 개별 체크박스 변경 → 전체동의 자동 업데이트
        individualChecks.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                checkAll.isChecked = individualChecks.all { it.isChecked }
            }
        }
    }

    /* [필수] 체크박스 2개 모두 체크되어야 버튼 활성화 */
    private fun setupRequiredCheckFeature() {
        val requiredChecks = listOf(checkAge, checkTerms)

        disableNextButton()

        requiredChecks.forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ ->
                isRequiredChecked = requiredChecks.all { it.isChecked }

                if (isRequiredChecked) {
                    enableNextButton()   // 둘 다 체크되면 파란색
                } else {
                    disableNextButton() // 하나라도 빠지면 회색
                }
            }
        }
    }


    /* 다음 버튼 활성 ↔ 비활성 */
    private fun enableNextButton() {  // 버튼 활성화 상태
        nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        nextButton.backgroundTintList = null
    }


    private fun disableNextButton() {  // 버튼 비활성화 상태
        nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        nextButton.backgroundTintList =
            ColorStateList.valueOf(requireContext().getColor(R.color.black_200))
    }

    /* [필수] 항목 하나라도 미체크 시 에러 박스 표시 */
    private fun showRequiredError(rootView: View) {
        val errorBox = rootView.findViewById<TextView>(R.id.requiredErrorText)
        errorBox.alpha = 0f
        errorBox.visibility = View.VISIBLE
        errorBox.animate().alpha(1f).setDuration(300).start()

        // 2초 뒤 자동 사라짐
        errorBox.postDelayed({
            errorBox.animate().alpha(0f).setDuration(300)
                .withEndAction { errorBox.visibility = View.GONE }
                .start()
        }, 2000)
    }

    /* popup 띄우기 */
    private fun showTermsPopup() {
        val dialog = Dialog(requireContext())
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_terms, null)

        dialog.setContentView(popupView)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
            setGravity(Gravity.CENTER) // 중앙 정렬
        }

        val closeIcon = popupView.findViewById<ImageView>(R.id.closeIcon)
        closeIcon.setOnClickListener { dialog.dismiss() }

        dialog.show()

        val widthInPx = (320 * resources.displayMetrics.density).toInt()
        val heightInPx = (347 * resources.displayMetrics.density).toInt()
        dialog.window?.setLayout(widthInPx, heightInPx)

    }
}
