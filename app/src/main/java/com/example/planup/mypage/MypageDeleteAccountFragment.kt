package com.example.planup.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageDeleteAccountBinding

class MypageDeleteAccountFragment: Fragment(){
    lateinit var binding: FragmentMypageDeleteAccountBinding
    lateinit var name: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageDeleteAccountBinding.inflate(inflater,container,false)
        clickListener()

        /*사용자 이름 받아와야 함*/
        //binding.deleteAccountNameTv.text = name

        return binding.root
    }

    private fun clickListener() {
        val selectedColor = ContextCompat.getColor(context as MainActivity, R.color.semanticB1)
        val unselectedColor = ContextCompat.getColor(context as MainActivity, R.color.semanticB4)

        /*뒤로가기*/
        binding.deleteAccountBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageOtherFragment())
                .commitAllowingStateLoss()
        }

        /*회원 탈퇴 체크박스*/
        binding.deleteAccountCheckIv.setOnClickListener{
            /*체크박스 체크 이미지 필요함*/
            /*회원 탈퇴하기 비활성화 버튼 필요함*/
            binding.deleteAccountCheckTv.setTextColor(selectedColor)
            binding.deleteAccountBtnIv.visibility =View.VISIBLE
        }
        /*회원 탈퇴 체크박스*/
        /*체크박스 체크 이미지 필요함*/
        /*회원 탈퇴하기 비활성화 버튼 필요함*/
        binding.deleteAccountCheckIv.setOnClickListener{
            /*체크박스 체크 이미지 필요함*/
            /*회원 탈퇴하기 비활성화 버튼 필요함*/
            binding.deleteAccountCheckTv.setTextColor(unselectedColor)
            binding.deleteAccountBtnIv.visibility =View.VISIBLE
        }

        /*회원 탈퇴 버튼*/
        binding.deleteAccountBtnIv.setOnClickListener{
            /*회원 탈퇴 로직 필요함*/
            /*로그인 or 회원가입 액티비티 연결 되어야 함*/
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container,LoginActivity())
//                .commitAllowingStateLoss()
            Toast.makeText(context,"회원 탈퇴 완료",LENGTH_SHORT).show()
        }
    }
}