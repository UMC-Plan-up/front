package com.example.planup.mypage

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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
            ToastInvalidEmail.createToast(context as MainActivity)
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }

        binding.passwordEmailDropdownIv.setOnClickListener{
            /*드롭다운 구현
            * 선택 시*/
            mailAddr = binding.passwordEmailEt.text.toString() + "드롭다운 결과"
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
    /* 이메일 주소 입력란 공란 여부 확인 */
    private fun textListener(){
        binding.passwordEmailEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                checkMail()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
    /*이메일로 인증 링크 받기 버튼 활성화*/
    private fun checkMail() {
        if(binding.passwordEmailEt.text.toString().isNotEmpty()
        ) {
            binding.btnPasswordLinkTv.isActivated = true
        }else{
            binding.btnPasswordLinkTv.isActivated = false
        }
    }
}