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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.login.LoginActivityNew
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.adapter.TermItemAdapter
import com.example.planup.signup.data.TermItem
import kotlinx.coroutines.launch
import com.example.planup.databinding.FragmentAgreementBinding
import com.example.planup.databinding.PopupTermsBinding

class AgreementFragment : Fragment() {

    private var _binding: FragmentAgreementBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TermItemAdapter
    private val termsList = mutableListOf<TermItem>()

    private var isRequiredChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.requiredErrorText.visibility = View.GONE

        /* 뒤로가기 아이콘 → 로그인 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivityNew::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        adapter = TermItemAdapter(
            termsList,
            onCheckedChanged = { checkRequiredAgreement() },
            onDetailClicked = { showTermsDetailPopup(it) }
        )
        binding.termsRecyclerView.adapter = adapter
        binding.termsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 약관 목록 불러오기
        fetchTermsList()

        // 전체동의 체크박스
        binding.checkAll.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAllChecked(isChecked)
            checkRequiredAgreement()
        }

        /* 다음 버튼 클릭 */
        binding.nextButton.setOnClickListener {
            if (isRequiredChecked) {
                saveAgreements()
                openNextStep()
            } else {
                showRequiredError()
            }
        }
        disableNextButton()
    }

    /* 약관 목록 불러오는 API 요청 */
    private fun fetchTermsList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsList()
                val body = response.body()

                val result = body?.result

                if (response.isSuccessful && !result.isNullOrEmpty()) {
                    termsList.clear()
                    termsList.addAll(result)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "약관 불러오기 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
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
        val agreements = termsList.map { term ->
            SignupActivity.Agreement(
                termsId = term.id,
                isAgreed = adapter.getCheckedTerms().any { it.id == term.id }
            )
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
        binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        binding.nextButton.backgroundTintList = null
    }

    /* 다음 버튼 비활성화 */
    private fun disableNextButton() {
        binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        binding.nextButton.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.black_200))
    }

    /* 필수 미체크 시 에러 표시 */
    private fun showRequiredError() {
        binding.requiredErrorText.alpha = 0f
        binding.requiredErrorText.visibility = View.VISIBLE
        binding.requiredErrorText.animate().alpha(1f).setDuration(300).start()
        binding.requiredErrorText.postDelayed({
            binding.requiredErrorText.animate().alpha(0f).setDuration(300).withEndAction {
                binding.requiredErrorText.visibility = View.GONE
            }.start()
        }, 2000)
    }

    /* 약관 상세 팝업 띄우기 */
    private fun showTermsDetailPopup(termsId: Int) {
        val dialog = Dialog(requireContext())
        val popupBinding = PopupTermsBinding.inflate(LayoutInflater.from(requireContext()))

        dialog.setContentView(popupBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)

        // API로 약관 상세 불러오기
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsDetail(termsId)
                val body = response.body()

                if (response.isSuccessful && body?.result?.content != null) {
                    popupBinding.termTitle.text = body.result.content
                } else {
                    popupBinding.termTitle.text = "약관 내용을 불러올 수 없습니다."
                }
            } catch (e: Exception) {
                popupBinding.termTitle.text = "네트워크 오류가 발생했습니다."
            }
        }

        popupBinding.closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            (320 * resources.displayMetrics.density).toInt(),
            (347 * resources.displayMetrics.density).toInt()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
