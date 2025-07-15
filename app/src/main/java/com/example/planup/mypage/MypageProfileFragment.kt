package com.example.planup.mypage

import android.content.Context
import android.graphics.drawable.ColorDrawable
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
import com.example.planup.databinding.FragmentMypageProfileBinding

class MypageProfileFragment:Fragment() {

    lateinit var binding:FragmentMypageProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageProfileBinding.inflate(inflater,container,false)
        clickListener()
        textListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.nameBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        binding.completeBtnIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }

    /*닉네밍 검사*/
    private fun textListener(){
        val errorColor = ContextCompat.getColor(context, R.color.semanticR1)
        val noramlColor = ContextCompat.getColor(context, R.color.semanticB1)
        binding.nickNameEditEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if(20<binding.nickNameEditEt.text.toString().length){
                    binding.nickNameErrorTv.text = "20자 이내로 입력해 주세요"
                    binding.nickNameLv.setBackgroundColor(errorColor)
                }else{
                    binding.nickNameErrorTv.text = ""
                    binding.nickNameLv.setBackgroundColor(noramlColor)
                }
                if(/*중복 닉네임*/30<binding.nickNameEditEt.text.toString().length){
                    binding.nickNameErrorTv.text = "이미 사용 중인 닉네임입니다"
                    binding.nickNameLv.setBackgroundColor(errorColor)
                }else{
                    binding.nickNameErrorTv.text = ""
                    binding.nickNameLv.setBackgroundColor(noramlColor)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

        })
    }
}