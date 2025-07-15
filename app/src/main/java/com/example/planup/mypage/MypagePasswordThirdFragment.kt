package com.example.planup.mypage

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePasswordThirdBinding

class MypagePasswordThirdFragment : Fragment() {
    lateinit var binding: FragmentMypagePasswordThirdBinding
    /*새로운 비밀번호, 재확인 모두 통과하면 완료버튼 활성화*/
    private var length:Boolean = false
    private var special:Boolean = false
    private var recheck:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordThirdBinding.inflate(inflater, container, false)
        clickListener()
        textListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.passwordThirdBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePasswordSecondFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 재설정 완료버튼*/
        binding.passwordThirdCompleteActivateIv.setOnClickListener{
            binding.passwordThirdCompleteActivateIv.visibility = View.GONE
            binding.passwordThirdCompleteDeactivateIv.visibility = View.VISIBLE
            makePopup()
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

        val satisfiedColor = ContextCompat.getColor(context as MainActivity,R.color.information3)
        val unsatisfiedColor = ContextCompat.getColor(context as MainActivity,R.color.semanticB4)

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
                binding.passwordThirdNewSpecialOnIv.visibility = View.VISIBLE
                binding.passwordThirdNewSpecialOffIv.visibility = View.GONE
                binding.passwordThirdNewSpecialTv.setTextColor(unsatisfiedColor)
                special = false
            }
        }

        /*비밀번호 재확인*/
        if (binding.passwordThirdCheckEnterEt.text.toString().isNotEmpty() &&
            binding.passwordThirdNewEnterEt.text.toString().equals(binding.passwordThirdNewEnterEt.text.toString())){
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
        if(length && special && recheck) binding.passwordThirdCompleteActivateIv.visibility = View.VISIBLE
        else binding.passwordThirdCompleteActivateIv.visibility = View.GONE
    }

    /*비밀번호 재설정 완료 팝업*/
    private fun makePopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_password_reset)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
        }
        dialog.findViewById<View>(R.id.popup_password_reset_iv).setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageFragment())
                .commitAllowingStateLoss()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

}