package com.example.planup.signup.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.login.LoginActivity
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import kotlinx.coroutines.launch

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


        // "자세히" 버튼 클릭 시 → 약관 상세 팝업 띄우기
        val detail1 = view.findViewById<TextView>(R.id.detail1)
        val detail2 = view.findViewById<TextView>(R.id.detail2)
        val detail3 = view.findViewById<TextView>(R.id.detail3)

        detail1.setOnClickListener { showTermsDetailPopup(termsId = 2) } // 서비스 이용약관
        detail2.setOnClickListener { showTermsDetailPopup(termsId = 3) } // 마케팅 수신
        detail3.setOnClickListener { showTermsDetailPopup(termsId = 4) } // 광고성 정보

        /* 다음 버튼 클릭 → LoginEmailFragment로 이동 */
        nextButton.setOnClickListener {
            if (isRequiredChecked) {
                // [필수] 모두 선택됨 → agreements 저장하고 LoginEmailFragment로 이동
                saveAgreements()
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
    private fun enableNextButton() {
        nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        nextButton.backgroundTintList = null
    }

    private fun disableNextButton() {
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
    private fun showTermsDetailPopup(termsId: Int) {
        val dialog = Dialog(requireContext())
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_terms, null)
        val contentText = popupView.findViewById<TextView>(R.id.termTitle)

        dialog.setContentView(popupView)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }

        // 서버에서 약관 상세 내용 불러오기
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsDetail(termsId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    contentText.text = response.body()?.result?.content
                } else {
                    contentText.text = "약관 내용을 불러올 수 없습니다."
                }
            } catch (e: Exception) {
                contentText.text = "네트워크 오류가 발생했습니다."
            }
        }

        popupView.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        val widthInPx = (320 * resources.displayMetrics.density).toInt()
        val heightInPx = (347 * resources.displayMetrics.density).toInt()
        dialog.window?.setLayout(widthInPx, heightInPx)
    }


    /* agreements 값 저장하는 함수 */
    private fun saveAgreements() {
        val activity = requireActivity() as SignupActivity
        val agreements = mutableListOf<SignupActivity.Agreement>()

        agreements.add(SignupActivity.Agreement(termsId = 1, isAgreed = checkAge.isChecked))   // 만 14세 이상 동의
        agreements.add(SignupActivity.Agreement(termsId = 2, isAgreed = checkTerms.isChecked)) // 이용약관 동의
        agreements.add(SignupActivity.Agreement(termsId = 3, isAgreed = checkMarketing.isChecked)) // 마케팅 수신 동의
        agreements.add(SignupActivity.Agreement(termsId = 4, isAgreed = checkAd.isChecked))         // 광고성 정보 수신

        activity.agreements = agreements
    }
}
