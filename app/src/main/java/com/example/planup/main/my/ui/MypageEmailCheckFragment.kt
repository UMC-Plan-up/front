package com.example.planup.main.my.ui

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageEmailCheckBinding
import com.example.planup.main.my.adapter.EmailLinkAdapter
import com.example.planup.main.my.adapter.SignupLinkAdapter
import com.example.planup.network.controller.UserController

class MypageEmailCheckFragment : Fragment(), EmailLinkAdapter {

    lateinit var binding: FragmentMypageEmailCheckBinding
    lateinit var mailAddr: String
    lateinit var popupView: View
    private var lastDomain: Int = 0 //가장 최근에 선택된 도메인
    private var curDomain: Int = 0 //지금 선택된 도메인
    private var curCheck: Int = 0 //지금 선택된 도메인에 체크
    lateinit var inputMethodManager: InputMethodManager

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailCheckBinding.inflate(inflater, container, false)
        init()
        manageKeyboard()
        clickListener()
        textListener()

        return binding.root
    }

    private fun init(){
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
        editor = prefs.edit()
    }
    private fun manageKeyboard() {
        inputMethodManager =
            (context as MainActivity).getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            (context as MainActivity).currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    // 터치 이벤트
    private fun clickListener() {
        /*뒤로 가기*/
        binding.backIv.setOnClickListener {
            (context as MainActivity).navigateToFragment(MypageFragment())
        }
        /*인증번호 받기 버튼 클릭*/
        binding.btnGetLinkTv.setOnClickListener {
            if (!binding.btnGetLinkTv.isActivated) return@setOnClickListener
            /*이메일 형식 확인 후 이메일 유효성 검사
            * 적절한 이메일인 경우 이메일 링크 수신여부 확인 페이지로 이동*/
            checkMail()
        }
        /* 이메일 도메인 드롭다운 */
        binding.emailDropdownIv.setOnClickListener {
            dropDown(binding.emailDropdownAnchor)
        }
        //외부 터치 시 키보드 사라짐
        binding.root.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                manageKeyboard()
                return false
            }

        })

    }

    //이메일 입력 여부 확인: 이메일이 입력되어야 다음 버튼 활성화 됨
    private fun textListener() {
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.emailEt.text.toString().isNotEmpty()) {
                    binding.btnGetLinkTv.isActivated = true
                } else {
                    binding.btnGetLinkTv.isActivated = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // 이메일 유효성 확인: 형식과 중복 여부 확인
    private fun checkMail() {
        val findAt = binding.emailEt.text.toString().indexOf('@', 0)
        val findDot = binding.emailEt.text.toString().indexOf('.', 0)

        if (findAt == -1 || findDot == -1) //이메일 형식 확인
            showToast(context as MainActivity, R.string.toast_incorrect_email)
        else { //이메일로 인증 링크 보내기
            val emailService = UserController()
            emailService.setEmailLinkAdapter(this)
            emailService.emailLinkService(binding.emailEt.text.toString())
        }
    }

    // 이메일 도메인 드롭다운 메뉴로 제공
    private fun dropDown(view: View) {
        val inflater = LayoutInflater.from(context)
        popupView = inflater.inflate(R.layout.dropdown_email_domain, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(view)
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))


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

    // 선택한 도메인을 드롭다운에 표시
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

    // 드롭다운에서 선택한 이메일 도메인 추가
    private fun mailEditor(domain: String): String {
        val emailInput = binding.emailEt.text.toString()
        val atIndex = emailInput.indexOf('@')
        return if (atIndex != -1) {
            "${emailInput.substring(0, atIndex)}@$domain"
        } else {
            "$emailInput@$domain"
        }
    }

    // 토스트 메시지 작성
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

    //인증링크 전송 성공
    override fun successEmailLink(email: String) {
        val emailLinkFragment = MypageEmailLinkFragment()
        editor.putString("newEmail", email)
        editor.apply()
        emailLinkFragment.arguments = Bundle().apply {
            putBoolean("deepLink",false)
        }
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, emailLinkFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    //인증 링크 전송 오류: 토스트 메시지 출력
    override fun failEmailLink(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

}