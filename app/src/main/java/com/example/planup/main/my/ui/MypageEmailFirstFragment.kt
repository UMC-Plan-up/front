package com.example.planup.main.my.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageEmailFirstBinding

class MypageEmailFirstFragment : Fragment() {

    lateinit var binding: FragmentMypageEmailFirstBinding
    lateinit var mailAddr: String
    lateinit var popupWindow: PopupWindow

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
            popupWindow.dismiss()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        /*인증번호 받기 버튼 클릭*/
        binding.btnGetLinkTv.setOnClickListener {
            if (!binding.btnGetLinkTv.isActivated) return@setOnClickListener
            mailAddr = binding.emailEt.text.toString()
            Toast.makeText(context,mailAddr,LENGTH_SHORT).show()

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailSecondFragment())
                .commitAllowingStateLoss()
        }
        /* 이메일 도메인 드롭다운 */
        binding.emailDropdownIv.setOnClickListener{
            dropDown(binding.emailEt)
        }
    }

    private fun dropDown(view: View){
        var inflater = LayoutInflater.from(context)
        var popupView = inflater.inflate(R.layout.dropdown_email_domain,null)

        popupWindow = PopupWindow(
            popupView,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.showAsDropDown(view)
        popupWindow.isOutsideTouchable = true
    }
    private fun textListener(){
        binding.emailEt.addTextChangedListener(object :TextWatcher {
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
        }else{
            binding.btnGetLinkTv.isActivated = false
        }
    }
}