package com.example.planup.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageEmailFirstBinding

class MypageEmailFirstFragment : Fragment() {

    lateinit var binding: FragmentMypageEmailFirstBinding
    lateinit var mailAddr: String
    lateinit var mailDomain: String

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

//    override fun onStart() {
//        super.onStart()
//
//    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.emailFirstBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        /*인증번호 받기 버튼 클릭*/
        binding.emailFirstReceiveBtnActIv.setOnClickListener {
            mailAddr =
                binding.emailFirstEt.text.toString() + "@" + binding.emailFirstDomainAtv.text.toString()
            Toast.makeText(context,mailAddr,LENGTH_SHORT).show()

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailSecondFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun textListener(){
        binding.emailFirstEt.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkMail()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.emailFirstDomainAtv.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkMail()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkMail() {
        if (binding.emailFirstEt.text.toString().isNotEmpty() && binding.emailFirstDomainAtv.text.toString().isNotEmpty()) {
            binding.emailFirstReceiveBtnDeactIv.visibility = View.GONE
            binding.emailFirstReceiveBtnActIv.visibility = View.VISIBLE
        }else{
            binding.emailFirstReceiveBtnDeactIv.visibility = View.VISIBLE
            binding.emailFirstReceiveBtnActIv.visibility = View.GONE
        }
    }
}