package com.example.planup.main.my.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePasswordFirstBinding

class MypagePasswordFirstFragment : Fragment() {

    lateinit var binding: FragmentMypagePasswordFirstBinding
    lateinit var mailAddr: String
    lateinit var popupView: View //이메일 드롭다운 아이템
    var curCheck: Int = 0 //지금 선택된 도메인에 체크
    var curDomain: Int = 0 //지금 선택된 도메인
    var lastDomain: Int = 0 //가장 최근에 선택된 도메인

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordFirstBinding.inflate(inflater, container, false)

        clickListener()
        textListener()
        return binding.root
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.passwordFirstBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }

        binding.emailDropdownIv.setOnClickListener {
            dropDown(binding.emailEt)
        }

        /*이메일로 인증 링크 받기*/
        binding.btnPasswordLinkTv.setOnClickListener {
            /* 버튼 활성화된 경우만 클릭 이벤트 처리함 */
            if (!binding.btnPasswordLinkTv.isActivated) return@setOnClickListener

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordSecondFragment())
                .commitAllowingStateLoss()

            /*형식 오류와 인증번호 버튼 활성화 기준이 필요함*/
//            if (binding.myPasswordEmailAddrEt.text.toString()
//                    .isNotEmpty() && binding.myPasswordEmailDomainAtv.text.toString().isNotEmpty()
//            ) {
//                mailAddr =
//                    binding.myPasswordEmailAddrEt.text.toString() + "@" + binding.myPasswordEmailDomainAtv.text.toString()
//
//                (context as MainActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_container, MypagePasswordSecondFragment())
//                    .commitAllowingStateLoss()
//            }else if(binding.myPasswordEmailAddrEt.text.toString()
//                    .isEmpty() || binding.myPasswordEmailDomainAtv.text.toString().isEmpty()){
//                ToastIncorrectEmail.createToast(context as MainActivity)
//            }else{
//                /*이메일 유효성 검사 로직 필요*/
//                ToastInvalidEmail.createToast(context as MainActivity)
//            }
        }
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
    private fun showSelected(selectedTv: Int, selectedIv: Int) {
        val colorSelected = ContextCompat.getColor(context, R.color.blue_300) //선택된 도메인
        val colorUnselected = ContextCompat.getColor(context, R.color.email_domain) //나머지 도메인

        //첫 선택인 경우
        if (curCheck == 0) {
            curCheck = selectedIv
            curDomain = selectedTv
        }
        //기존 도메인 선택 해제
        popupView.findViewById<View>(curCheck).visibility = View.GONE
        popupView.findViewById<TextView>(curDomain).setTextColor(colorUnselected)
        //선택된 도메인 표시
        popupView.findViewById<View>(selectedIv).visibility = View.VISIBLE
        popupView.findViewById<TextView>(selectedTv).setTextColor(colorSelected)
        //선택된 도메인 멉데이트
        curCheck = selectedIv
        curDomain = selectedTv
    }

    /* 드롭다운에서 선택한 이메일 도메인 추가 */
    private fun mailEditor(domain: String): String {
        val emailInput = binding.emailEt.text.toString()
        val atIndex = emailInput.indexOf('@')
        return if (atIndex != -1) {
            "${emailInput.substring(0, atIndex-1)}@$domain"
        } else {
            "$emailInput@$domain"
        }
    }

    /* 이메일 주소 입력란 공란 여부 확인 */
    private fun textListener() {
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkMail()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /*이메일로 인증 링크 받기 버튼 활성화*/
    private fun checkMail() {
        if (binding.emailEt.text.toString().isNotEmpty()
        ) {
            binding.btnPasswordLinkTv.isActivated = true
        } else {
            binding.btnPasswordLinkTv.isActivated = false
        }
    }
}