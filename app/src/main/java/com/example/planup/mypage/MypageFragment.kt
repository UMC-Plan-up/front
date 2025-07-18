package com.example.planup.mypage

import android.graphics.drawable.ColorDrawable
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        clickListener()
        return binding.root
    }

    private fun clickListener(){

        /*프로필 사진 변경*/
        binding.mypageMainRewriteIv.setOnClickListener{
            showPopupMenu(binding.mypageMainRewriteIv)
        }

        /*닉네임 변경*/
        binding.mypageNicknameIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageProfileFragment())
                .commitAllowingStateLoss()
        }
        /*이메일 변경*/
        binding.mypageEmailIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageEmailFirstFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 변경*/
        binding.mypagePasswordIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePasswordFirstFragment())
                .commitAllowingStateLoss()
        }
        /*카카오톡 계정 연동*/
        binding.mypageKakaoIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageKakaoFragment())
                .commitAllowingStateLoss()
        }
        /*기타 계정 관리*/
        binding.mypageOtherIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageOtherFragment())
                .commitAllowingStateLoss()
        }
        /*차단 친구 관리*/
        binding.mypageFriendBlockIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageFriendBlockFragment())
                .commitAllowingStateLoss()
        }

//        /*인원 제한 관리*/
//        binding.mypageFriendNumberIv.setOnClickListener{
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container,MypageFriendLimitFragment())
//                .commitAllowingStateLoss()
//        }

        /*서비스 알림*/
        binding.mypageAlertServiceIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageServiceFragment())
                .commitAllowingStateLoss()
        }

        /*혜택 및 마케팅 알림*/
        binding.mypageKakaoIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePolicyFragment())
                .commitAllowingStateLoss()
        }
        /*마케팅 수신 알림 토글*/
        binding.mypageAlertBenefitOnIv.setOnClickListener{
            binding.mypageAlertBenefitOnIv.visibility=View.GONE
            binding.mypageAlertBenefitOffIv.visibility=View.VISIBLE
        }
        binding.mypageAlertBenefitOffIv.setOnClickListener{
            binding.mypageAlertBenefitOnIv.visibility=View.VISIBLE
            binding.mypageAlertBenefitOffIv.visibility=View.GONE
        }

        /*이용약관 및 정책*/
        binding.mypagePolicyIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePolicyFragment())
                .commitAllowingStateLoss()
        }



    }

    /*프로필 사진 재설정 드로다운 메뉴*/
    private fun showPopupMenu(view : View) {

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dropdown_profile, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // 포커스 가능
        )

        // 팝업 바깥 클릭 시 닫힘 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable())

        // 팝업 표시 (예: 이미지뷰 아래에)
        popupWindow.showAsDropDown(view)

        popupView.findViewById<View>(R.id.album_cl).setOnClickListener{
            Toast.makeText(context,"앨범 선택",LENGTH_SHORT).show()
        }
        popupView.findViewById<View>(R.id.photo_cl).setOnClickListener{
            Toast.makeText(context,"사진 선택",LENGTH_SHORT).show()
        }
        popupView.findViewById<View>(R.id.file_cl).setOnClickListener{
            Toast.makeText(context,"파일 선택",LENGTH_SHORT).show()
        }


    }
}