package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.databinding.FragmentMypagePasswordChangeBinding
import com.example.planup.main.my.adapter.PasswordChangeAdapter
import com.example.planup.network.controller.UserController

class MypagePasswordChangeFragment : Fragment(),PasswordChangeAdapter {
    lateinit var binding: FragmentMypagePasswordChangeBinding
    /*새로운 비밀번호, 재확인 모두 통과하면 완료버튼 활성화*/
    private var length:Boolean = false
    private var special:Boolean = false
    private var recheck:Boolean = false

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordChangeBinding.inflate(inflater, container, false)
        init()
        clickListener()
        textListener()
        return binding.root
    }

    private fun init(){
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
    }
    private fun clickListener(){
        /*뒤로 가기*/
        binding.passwordThirdBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordLinkFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 재설정 완료버튼*/
        binding.btnPasswordCompleteTv.setOnClickListener{
            if (!binding.btnPasswordCompleteTv.isActivated) return@setOnClickListener
            val service = UserController()
            service.setPasswordChangeAdapter(this)
            service.passwordUpdateService(binding.passwordThirdCheckEnterEt.text.toString())
        }
    }

    private fun textListener() {
        binding.passwordThirdNewEnterEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkPassword()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        binding.passwordThirdCheckEnterEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkPassword()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /*비밀번호 유효성 검사*/
    private fun checkPassword() {

        val satisfiedColor = ContextCompat.getColor(context as MainActivity,R.color.green_200)
        val unsatisfiedColor = ContextCompat.getColor(context as MainActivity,R.color.black_300)

        /*새로운 비밀번호 설정*/
        if (binding.passwordThirdNewEnterEt.text.toString().isNotEmpty()) {
            /*비밀번호 길이 검사*/
            if (7<binding.passwordThirdNewEnterEt.text.toString().length && binding.passwordThirdNewEnterEt.text.toString().length<21) {
                binding.passwordThirdNewLengthOnIv.visibility = View.VISIBLE
                binding.passwordThirdNewLengthOffIv.visibility = View.GONE
                binding.passwordThirdNewLengthTv.setTextColor(satisfiedColor)
                length = true
            }else{
                binding.passwordThirdNewLengthOnIv.visibility = View.GONE
                binding.passwordThirdNewLengthOffIv.visibility = View.VISIBLE
                binding.passwordThirdNewLengthTv.setTextColor(unsatisfiedColor)
                length = false
            }

            /*특수문자 포함 여부 확인*/
            val hasDigit = binding.passwordThirdNewEnterEt.text.toString().any { it.isDigit() }
            val hasSpecial = binding.passwordThirdNewEnterEt.text.toString().matches(".*[^a-zA-Z0-9].*".toRegex())

            if (hasDigit && hasSpecial) {
                binding.passwordThirdNewSpecialOnIv.visibility = View.VISIBLE
                binding.passwordThirdNewSpecialOffIv.visibility = View.GONE
                binding.passwordThirdNewSpecialTv.setTextColor(satisfiedColor)
                special = true
            }else{
                binding.passwordThirdNewSpecialOnIv.visibility = View.GONE
                binding.passwordThirdNewSpecialOffIv.visibility = View.VISIBLE
                binding.passwordThirdNewSpecialTv.setTextColor(unsatisfiedColor)
                special = false
            }
        }

        /*비밀번호 재확인*/
        if (binding.passwordThirdCheckEnterEt.text.toString().isNotEmpty() &&
            binding.passwordThirdNewEnterEt.text.toString().equals(binding.passwordThirdCheckEnterEt.text.toString())){
            binding.passwordThirdCheckCorrectOnIv.visibility = View.VISIBLE
            binding.passwordThirdCheckCorrectOffIv.visibility = View.GONE
            binding.passwordThirdCheckCorrectTv.setTextColor(satisfiedColor)
            recheck = true
        }else{
            binding.passwordThirdCheckCorrectOnIv.visibility = View.GONE
            binding.passwordThirdCheckCorrectOffIv.visibility = View.VISIBLE
            binding.passwordThirdCheckCorrectTv.setTextColor(unsatisfiedColor)
            recheck = false
        }
        if(length && special && recheck){
            binding.btnPasswordCompleteTv.isActivated = true
        }
        else{
            binding.btnPasswordCompleteTv.isActivated = false
        }
    }

    /*비밀번호 재설정 완료 팝업*/
    private fun makePopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_password_changed)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            //배경 투명색
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            //외부 터치 불가
            dialog.setCanceledOnTouchOutside(false)
        }
        //~님의 비밀번호가 변경되었어요 메시지에 사용자 이메일 바인딩 하기
        dialog.findViewById<TextView>(R.id.popup_password_sub_tv).text = getString(R.string.popup_password_explain,prefs.getString("email","null"))
        //학인버튼 클릭 시 팝업 종료 및 마이페이지로 이동
        dialog.findViewById<View>(R.id.popup_password_reset_btn).setOnClickListener{
            dialog.dismiss()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        dialog.show()
    }

    override fun successPasswordChange() {
        makePopup()
    }

    override fun failPasswordChange(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }

}