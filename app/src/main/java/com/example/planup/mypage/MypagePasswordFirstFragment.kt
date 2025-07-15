package com.example.planup.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePasswordFirstBinding
import com.example.planup.toast.ToastIncorrectEmail
import com.example.planup.toast.ToastInvalidEmail

class MypagePasswordFirstFragment : Fragment() {

    lateinit var binding: FragmentMypagePasswordFirstBinding
    lateinit var mailAddr: String

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

        /*이메일로 인증 링크 받기*/
        binding.passwordFirstReceiveBtnActIv.setOnClickListener {

            /*형식 오류와 인증번호 버튼 활성화 기준이 필요함*/
            if (binding.myPasswordEmailAddrEt.text.toString()
                    .isNotEmpty() && binding.myPasswordEmailDomainAtv.text.toString().isNotEmpty()
            ) {
                mailAddr =
                    binding.myPasswordEmailAddrEt.text.toString() + "@" + binding.myPasswordEmailDomainAtv.text.toString()

                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, MypagePasswordSecondFragment())
                    .commitAllowingStateLoss()
            }else if(binding.myPasswordEmailAddrEt.text.toString()
                    .isEmpty() || binding.myPasswordEmailDomainAtv.text.toString().isEmpty()){
                ToastIncorrectEmail.createToast(context as MainActivity)
            }else{
                /*이메일 유효성 검사 로직 필요*/
                ToastInvalidEmail.createToast(context as MainActivity)
            }
        }
    }
    private fun textListener(){
        binding.myPasswordEmailAddrEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                checkMail()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        binding.myPasswordEmailDomainAtv.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                checkMail()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun checkMail() {
        if(binding.myPasswordEmailAddrEt.text.toString().isNotEmpty()
            && binding.myPasswordEmailDomainAtv.text.toString().isNotEmpty()
        ) {
            binding.passwordFirstReceiveBtnActIv.visibility = View.VISIBLE
            binding.passwordFirstReceiveBtnDeactIv.visibility = View.GONE
        }else{
            binding.passwordFirstReceiveBtnActIv.visibility = View.GONE
            binding.passwordFirstReceiveBtnDeactIv.visibility = View.VISIBLE
        }
    }
}