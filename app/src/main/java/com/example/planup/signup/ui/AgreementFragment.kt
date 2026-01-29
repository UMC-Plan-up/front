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
import com.example.planup.R
import com.example.planup.login.ui.LoginActivityNew
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.adapter.TermItemAdapter
import com.example.planup.network.dto.term.TermItem
import kotlinx.coroutines.launch
import com.example.planup.databinding.FragmentAgreementBinding
import com.example.planup.databinding.PopupTermsBinding
import kotlin.math.min

class AgreementFragment : Fragment() {

    private var _binding: FragmentAgreementBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TermItemAdapter
    private val termsList = mutableListOf<TermItem>()
    private var isRequiredChecked = false

    private val titleOverrideMap = mapOf(
        2 to "서비스 이용약관",
        3 to "개인정보 수집 및 이용 동의",
        4 to "홍보 및 마케팅 정보 수집·이용 동의서",
        5 to "광고성 정보 수신 동의"
    )

    companion object {
        private const val TAG = "AgreementFragment"
    }

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

        binding.backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivityNew::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        adapter = TermItemAdapter(
            termsList,
            onCheckedChanged = {
                Log.d(TAG, "onCheckedChanged() 호출")
                checkRequiredAgreement()
                debugDump()
            },
            onDetailClicked = { showTermsDetailPopup(it) }
        )
        binding.termsRecyclerView.adapter = adapter
        binding.termsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchTermsList()

        binding.checkAll.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "checkAll changed: $isChecked")
            adapter.setAllChecked(isChecked)
            checkRequiredAgreement()
            debugDump()
        }

        binding.nextButton.setOnClickListener {
            Log.d(TAG, "nextButton clicked, isRequiredChecked=$isRequiredChecked")
            if (isRequiredChecked) {
                saveAgreements()
                openNextStep()
            } else {
                showRequiredError()
            }
        }
        disableNextButton()
    }

    private fun fetchTermsList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.termsApi.getTermsList()
                val body = response.body()
                val result = body?.result

                if (response.isSuccessful && !result?.termsList.isNullOrEmpty()) {
                    termsList.clear()
                    termsList.addAll(result.termsList)
                    adapter.notifyDataSetChanged()
                    Log.d(
                        TAG,
                        "fetchTermsList: total=${termsList.size}, required=${termsList.count { it.isRequired }}, ids=${termsList.map { it.id }}"
                    )
                } else {
                    Toast.makeText(requireContext(), "약관 불러오기 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "fetchTermsList 실패: msg=${body?.message}")
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "fetchTermsList 예외: ${e.message}", e)
            }
        }
    }

    private fun checkRequiredAgreement() {
        val requiredTerms = termsList.filter { it.isRequired }
        val checkedRequired = adapter.getCheckedTerms().filter { it.isRequired }

        isRequiredChecked = requiredTerms.size == checkedRequired.size && requiredTerms.isNotEmpty()

        Log.d(
            TAG,
            "checkRequiredAgreement: requiredCount=${requiredTerms.size}, checkedRequiredCount=${checkedRequired.size}, isRequiredChecked=$isRequiredChecked"
        )

        if (isRequiredChecked) enableNextButton() else disableNextButton()
    }

    private fun debugDump() {
        val reqIds = termsList.filter { it.isRequired }.map { it.id }
        val chkIds = adapter.getCheckedTerms().filter { it.isRequired }.map { it.id }
        Log.d(TAG, "dump -> requiredIds=$reqIds, checkedRequiredIds=$chkIds")
    }

    private fun saveAgreements() {
        val activity = requireActivity() as SignupActivity
        val agreements = termsList.map { term ->
            SignupActivity.Agreement(
                termsId = term.id,
                isAgreed = adapter.getCheckedTerms().any { it.id == term.id }
            )
        }
        activity.agreements = agreements
        Log.d(TAG, "saveAgreements: ${agreements.joinToString { "${it.termsId}:${it.isAgreed}" }}")
    }

    private fun openNextStep() {
        val isKakaoSignup = arguments?.getBoolean("isKakaoSignup", false) ?: false
        Log.d(TAG, "openNextStep: isKakaoSignup=$isKakaoSignup")

        if (isKakaoSignup) {
            val tempUserId = arguments?.getString("tempUserId")
            val profileSetupBundle = Bundle().apply { putString("tempUserId", tempUserId) }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, ProfileSetupFragment().apply { arguments = profileSetupBundle })
                .addToBackStack(null)
                .commit()
        } else {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, LoginEmailFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun enableNextButton() {
        binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        binding.nextButton.backgroundTintList = null
        Log.d(TAG, "enableNextButton()")
    }

    private fun disableNextButton() {
        binding.nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        binding.nextButton.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.black_200))
        Log.d(TAG, "disableNextButton()")
    }

    private fun showRequiredError() {
        binding.requiredErrorText.alpha = 0f
        binding.requiredErrorText.visibility = View.VISIBLE
        binding.requiredErrorText.animate().alpha(1f).setDuration(300).start()
        binding.requiredErrorText.postDelayed({
            binding.requiredErrorText.animate().alpha(0f).setDuration(300).withEndAction {
                binding.requiredErrorText.visibility = View.GONE
            }.start()
        }, 2000)
        Log.d(TAG, "showRequiredError()")
    }

    private fun normalizedTitle(termsId: Int): String {
        titleOverrideMap[termsId]?.let { return it }
        val raw = termsList.firstOrNull { it.id == termsId }?.summary.orEmpty()
        val cleaned = raw.replace(Regex("""^\s*[\[\(]?(필수|선택)[\]\)]?\s*"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
        return cleaned.ifBlank { "약관" }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    /* 약관 상세 팝업 */
    private fun showTermsDetailPopup(termsId: Int) {
        val dialog = Dialog(requireContext())
        val b = PopupTermsBinding.inflate(layoutInflater)

        dialog.setContentView(b.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val w = (resources.displayMetrics.widthPixels * 0.9f).toInt()
        val targetH = min(dpToPx(420), (resources.displayMetrics.heightPixels * 0.8f).toInt())
        dialog.window?.setLayout(w, targetH)
        dialog.window?.setGravity(Gravity.CENTER)

        b.termTitle.text = normalizedTitle(termsId)
        b.termContent.text = ""

        lifecycleScope.launch {
            try {
                val resp = RetrofitInstance.termsApi.getTermsDetail(termsId)
                val body = resp.body()
                b.termContent.text = if (resp.isSuccessful && body?.result?.content != null)
                    body.result.content
                else "약관 내용을 불러올 수 없습니다."
            } catch (_: Exception) {
                b.termContent.text = "네트워크 오류가 발생했습니다."
            }
        }

        b.closeIcon.isClickable = true
        b.closeIcon.isFocusable = true
        b.closeIcon.bringToFront()
        b.closeIcon.setOnClickListener {
            Log.d(TAG, "close clicked")
            if (dialog.isShowing) dialog.dismiss()
        }

        dialog.setOnDismissListener { Log.d(TAG, "terms dialog dismissed") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
