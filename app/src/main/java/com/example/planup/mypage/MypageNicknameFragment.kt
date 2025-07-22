package com.example.planup.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageNicknameBinding

class MypageNicknameFragment:Fragment() {

    lateinit var binding:FragmentMypageNicknameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageNicknameBinding.inflate(inflater,container,false)
        clickListener()
        textListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.nicknameBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        binding.nicknameCompleteBtn.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }

    /*닉네임 검사*/
    private fun textListener(){
        val errorColor = ContextCompat.getColor(context, R.color.semanticR1)
        val normalColor = ContextCompat.getColor(context,R.color.black_400)
        binding.nicknameEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

                if (binding.nicknameEt.text.toString().length in 10..20){
                    //정상
                    binding.nickNameErrorTv.setText(null)
                    binding.nickNameLv.setBackgroundColor(normalColor)
                }else if (20 < binding.nicknameEt.text.toString().length){
                    //20자 초과
                    binding.nickNameErrorTv.setText(R.string.error_under_twenty_word)
                    binding.nickNameLv.setBackgroundColor(errorColor)
                }else if (binding.nicknameEt.text.toString().length < 10){
                    //중복 닉네임
                    binding.nickNameErrorTv.setText(R.string.error_already_nickname)
                    binding.nickNameLv.setBackgroundColor(errorColor)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }
}