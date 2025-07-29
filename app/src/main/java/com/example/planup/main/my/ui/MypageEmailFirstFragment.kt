package com.example.planup.main.my.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageEmailFirstBinding

class MypageEmailFirstFragment : Fragment() {

    lateinit var binding: FragmentMypageEmailFirstBinding
    lateinit var mailAddr: String
    lateinit var popupView: View
    private var lastDomain: Int = 0 //가장 최근에 선택된 도메인
    private var curDomain: Int = 0 //지금 선택된 도메인
    private var curCheck: Int = 0 //지금 선택된 도메인에 체크

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailFirstBinding.inflate(inflater, container, false)

        checkMail()
        clickListener()
        textListener()

        return binding.root
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.backIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        /*인증번호 받기 버튼 클릭*/
        binding.btnGetLinkTv.setOnClickListener {
            if (!binding.btnGetLinkTv.isActivated) return@setOnClickListener
            mailAddr = binding.emailEt.text.toString()
            showToast(context as MainActivity, R.string.toast_invalid_email)
            showToast(context as MainActivity, R.string.toast_incorrect_email)
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailSecondFragment())
                .commitAllowingStateLoss()
        }
        /* 이메일 도메인 드롭다운 */
        binding.emailDropdownIv.setOnClickListener {
            dropDown(binding.emailEt)
        }
    }

    private fun textListener() {
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkMail()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkMail() {
        if (binding.emailEt.text.toString().isNotEmpty()) {
            binding.btnGetLinkTv.isActivated = true
        } else {
            binding.btnGetLinkTv.isActivated = false
        }
    }

    private fun showToast(context: Context, message: Int) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(message)

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 300)
        toast.show()
    }

    /* 이메일 도메인 드롭다운 */
    private fun dropDown(view: View) {
        val inflater = LayoutInflater.from(context)
        popupView = inflater.inflate(R.layout.dropdown_email_domain, null)
        val popupWindow = PopupWindow(
            popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.showAsDropDown(view)
        popupWindow.isOutsideTouchable = true


        /* 지메일 선택 */
        popupView.findViewById<View>(R.id.email_domain_gmail_cl).setOnClickListener {
            if (lastDomain != 0
                && lastDomain == R.id.email_domain_gmail_cl
            ) return@setOnClickListener

            lastDomain = R.id.email_domain_gmail_cl //선택된 도메인 업데이트
            showSelected(R.id.gmail_tv, R.id.gmail_check_iv)
            mailAddr = mailEditor(getString(R.string.dropdown_gmail))//현재 선택된 도메인
            binding.emailEt.setText(mailAddr) //입력란에 최종 메일 주소 출력
            popupWindow.dismiss()
        }

        /*네이버 메일 선택*/
        popupView.findViewById<View>(R.id.email_domain_naver_cl).setOnClickListener {
            if (lastDomain != 0
                && lastDomain == R.id.email_domain_naver_cl
            ) return@setOnClickListener

            lastDomain = R.id.email_domain_naver_cl //선택된 도메인 업데이트
            showSelected(R.id.naver_tv, R.id.naver_check_iv)
            mailAddr = mailEditor(getString(R.string.dropdown_naver)) //입력한 메일주소 + 선택한 도메인
            binding.emailEt.setText(mailAddr) //입력란에 최종 메일 주소 출력
            popupWindow.dismiss()
        }

        /* 카카오 메일 선택 */
        popupView.findViewById<View>(R.id.email_domain_kakao_cl).setOnClickListener {
            if (lastDomain != 0
                && lastDomain == R.id.email_domain_kakao_cl
            ) return@setOnClickListener

            lastDomain = R.id.email_domain_kakao_cl //선택된 도메인 업데이트
            showSelected(R.id.kakao_tv, R.id.kakao_check_iv)
            mailAddr = mailEditor(getString(R.string.dropdown_kakao)) //현재 선택된 도메인
            binding.emailEt.setText(mailAddr) //입력란에 최종 메일 주소 출력
            popupWindow.dismiss()
        }
    }


    /* 드롭다운에서 선택한 이메일 도메인 표시 */
    private fun showSelected(selectedDomain: Int, checkSelected: Int) {
        val colorSelected = ContextCompat.getColor(context, R.color.blue_300) //선택된 도메인
        val colorUnselected = ContextCompat.getColor(context, R.color.email_domain) //나머지 도메인

        //첫 선택인 경우
        if (curCheck == 0) {
            curCheck = checkSelected
            curDomain = selectedDomain
        }
        //기존 도메인 선택 해제
        popupView.findViewById<View>(curCheck).visibility = View.GONE
        popupView.findViewById<TextView>(curDomain).setTextColor(colorUnselected)
        //선택된 도메인 표시
        popupView.findViewById<View>(checkSelected).visibility = View.VISIBLE
        popupView.findViewById<TextView>(selectedDomain).setTextColor(colorSelected)
        //선택된 도메인 멉데이트
        curCheck = checkSelected
        curDomain = selectedDomain
    }

    /* 드롭다운에서 선택한 이메일 도메인 추가 */
    private fun mailEditor(domain: String): String {
        val emailInput = binding.emailEt.text.toString()
        val atIndex = emailInput.indexOf('@')
        return if (atIndex != -1) {
            "${emailInput.substring(0, atIndex)}@$domain"
        } else {
            "$emailInput@$domain"
        }
    }
}