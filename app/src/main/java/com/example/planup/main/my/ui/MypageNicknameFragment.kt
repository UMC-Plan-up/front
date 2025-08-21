package com.example.planup.main.my.ui

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageNicknameBinding
import com.example.planup.main.my.adapter.NicknameChangeAdapter
import com.example.planup.network.controller.UserController

//마이페이지의 닉네임 변경 프레그먼트
class MypageNicknameFragment:Fragment(), NicknameChangeAdapter {

    lateinit var binding:FragmentMypageNicknameBinding
    lateinit var prefs: SharedPreferences
    lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageNicknameBinding.inflate(inflater,container,false)
        init()
        clickListener()
        textListener()
        return binding.root
    }

    //초기 세팅
    private fun init(){
        binding.mypageNicknameCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.mypageNicknameCl.height
                binding.mypageNicknameInnerCl.minHeight = height
                binding.mypageNicknameCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()
    }
    private fun clickListener(){
        //뒤로 가기
        binding.nicknameBackIv.setOnClickListener{
            (context as MainActivity).navigateToFragment(MypageFragment())
        }
        //완료 버튼 클릭: 마이페이지 화면으로 이동
        binding.nicknameCompleteBtn.setOnClickListener{
            val nicknameService = UserController()
            nicknameService.setNicknameChangeAdapter(this)
            nicknameService.nicknameService(binding.nicknameEt.text.toString())
        }
    }

    /*닉네임 검사*/
    private fun textListener(){
        val errorColor = ContextCompat.getColor(context, R.color.red_200)
        val normalColor = ContextCompat.getColor(context,R.color.black_400)
        binding.nicknameEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

                if (binding.nicknameEt.text.toString().length in 1..20){
                    //정상
                    binding.nickNameErrorTv.setText(null)
                    binding.nickNameLv.setBackgroundColor(normalColor)
                }else if (20 < binding.nicknameEt.text.toString().length){
                    //20자 초과
                    binding.nickNameErrorTv.setText(R.string.error_under_twenty_word)
                    binding.nickNameLv.setBackgroundColor(errorColor)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    //닉네임 변경 성공
    override fun successNicknameChange(nickname: String) {
        editor.putString("nickname",nickname)
        editor.apply()
        (context as MainActivity).navigateToFragment(MypageFragment())
    }
    //닉네임 변경 오류
    override fun failNicknameChange(message: String) {
        val errorColor = ContextCompat.getColor(context, R.color.semanticR1)
        binding.nickNameErrorTv.setText(R.string.error_already_nickname)
        binding.nickNameLv.setBackgroundColor(errorColor)

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