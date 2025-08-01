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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.login.ui.LoginActivity
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.adapter.TermItemAdapter
import com.example.planup.signup.data.TermItem
import kotlinx.coroutines.launch

class AgreementFragment : Fragment() {

    private lateinit var checkAll: CheckBox
    private lateinit var nextButton: Button
    private lateinit var termsRecyclerView: RecyclerView
    private lateinit var adapter: TermItemAdapter
    private val termsList = mutableListOf<TermItem>()

    private var isRequiredChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agreement, container, false)

        checkAll = view.findViewById(R.id.checkAll)
        nextButton = view.findViewById(R.id.nextButton)
        termsRecyclerView = view.findViewById(R.id.termsRecyclerView)

        val errorBox = view.findViewById<TextView>(R.id.requiredErrorText)
        errorBox.visibility = View.GONE

        /* 뒤로가기 아이콘 → 로그인 화면으로 이동 */
        view.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        adapter = TermItemAdapter(termsList,
            onCheckedChanged = { checkRequiredAgreement() },
            onDetailClicked = { showTermsDetailPopup(it) }
        )
        termsRecyclerView.adapter = adapter
        termsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 약관 목록 불러오기
        fetchTermsList()

        // 전체동의 체크박스
        checkAll.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAllChecked(isChecked)
            checkRequiredAgreement()
        }

        /* 다음 버튼 클릭 */
        nextButton.setOnClickListener {
            if (isRequiredChecked) {
                saveAgreements()
                openNextStep()
            } else {
                showRequiredError(view)
            }
        }

        return view
    }

    /* 약관 목록 불러오는 API 요청 */
    private fun fetchTermsList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsList()
                val body = response.body()

                Log.d("AgreementFragment", "📦 전체 응답 body: $body")

                // ✅ isSuccess를 신뢰하지 않고 result 기반으로 처리
                val result = body?.result

                if (response.isSuccessful && !result.isNullOrEmpty()) {
                    Log.d("AgreementFragment", "✅ 약관 ${result.size}개 불러오기 성공")
                    termsList.clear()
                    termsList.addAll(result)
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("AgreementFragment", "❌ result가 null이거나 비어있음, message: ${body?.message}")
                    Toast.makeText(requireContext(), "약관 불러오기 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AgreementFragment", "🔥 네트워크 오류: ${e.message}")
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    /* 필수 약관 모두 체크되었는지 검사 */
    private fun checkRequiredAgreement() {
        val requiredTerms = termsList.filter { it.isRequired }
        val checkedRequired = adapter.getCheckedTerms().filter { it.isRequired }

        isRequiredChecked = requiredTerms.size == checkedRequired.size

        if (isRequiredChecked) enableNextButton()
        else disableNextButton()
    }

    /* 사용자 선택 약관 저장 */
    private fun saveAgreements() {
        val activity = requireActivity() as SignupActivity
        val agreements = adapter.getCheckedTerms().map {
            SignupActivity.Agreement(it.id, true)
        }
        activity.agreements = agreements
    }

    /* 다음 화면으로 이동 */
    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginEmailFragment())
            .addToBackStack(null)
            .commit()
    }

    /* 다음 버튼 활성화 */
    private fun enableNextButton() {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        nextButton.backgroundTintList = null
    }

    /* 다음 버튼 비활성화 */
    private fun disableNextButton() {
        nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        nextButton.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.black_200))
    }

    /* 필수 미체크 시 에러 표시 */
    private fun showRequiredError(rootView: View) {
        val errorBox = rootView.findViewById<TextView>(R.id.requiredErrorText)
        errorBox.alpha = 0f
        errorBox.visibility = View.VISIBLE
        errorBox.animate().alpha(1f).setDuration(300).start()
        errorBox.postDelayed({
            errorBox.animate().alpha(0f).setDuration(300).withEndAction {
                errorBox.visibility = View.GONE
            }.start()
        }, 2000)
    }

    /* 약관 상세 팝업 띄우기 */
    private fun showTermsDetailPopup(termsId: Int) {
        val dialog = Dialog(requireContext())
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_terms, null)
        val contentText = popupView.findViewById<TextView>(R.id.termTitle)

        dialog.setContentView(popupView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)

        // API로 약관 상세 불러오기
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsDetail(termsId)

                val body = response.body()
                Log.d("AgreementFragment", "📄 상세 응답 body: $body")
                Log.d("AgreementFragment", "📄 상세 isSuccess: ${body?.isSuccess}")
                Log.d("AgreementFragment", "📄 상세 result: ${body?.result}")
                Log.d("AgreementFragment", "📄 상세 content: ${body?.result?.content}")

                if (response.isSuccessful && body?.result?.content != null) {
                    contentText.text = body.result.content
                } else {
                    contentText.text = "약관 내용을 불러올 수 없습니다."
                }
            } catch (e: Exception) {
                contentText.text = "네트워크 오류가 발생했습니다."
                Log.e("AgreementFragment", "🔥 예외: ${e.message}")
            }
        }


        popupView.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            (320 * resources.displayMetrics.density).toInt(),
            (347 * resources.displayMetrics.density).toInt()
        )
    }
}
