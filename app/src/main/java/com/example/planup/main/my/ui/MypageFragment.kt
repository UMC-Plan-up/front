package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageBinding
import com.example.planup.login.LoginActivityNew
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.network.controller.UserController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MypageFragment : Fragment(), ServiceAlertAdapter {
    lateinit var binding: FragmentMypageBinding

    //API 연동
    private lateinit var service: UserController
    //sharedPreferences
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    private fun init(){
        service = UserController()
        service.setServiceAdapter(this)
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
        editor = prefs.edit()
        binding.mypageMainEmailTv.text = prefs.getString("email","null").toString()
    }
    private fun clickListener(){

        binding.mypageBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())
                .commitAllowingStateLoss()
        }

        /*프로필 사진 변경*/
        binding.mypageMainRewriteIv.setOnClickListener{
            showDropdown(binding.mypageMainRewriteIv)
        }

        /*닉네임 변경*/
        binding.mypageNicknameIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageNicknameFragment())
                .commitAllowingStateLoss()
        }
        /*이메일 변경*/
        binding.mypageEmailIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailCheckFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 변경*/
        binding.mypagePasswordIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordEmailFragment())
                .commitAllowingStateLoss()
        }
        /*카카오톡 계정 연동*/
        binding.mypageKakaoIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageKakaoFragment())
                .commitAllowingStateLoss()
        }
        //기타 계정 관리
        binding.mypageOtherIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageOtherFragment())
                .commitAllowingStateLoss()
        }
        //차단 친구 관리
        binding.mypageFriendBlockIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFriendBlockFragment())
                .commitAllowingStateLoss()
        }

        //서비스 알림 수신 토글 끄기
        binding.mypageAlertServiceOnIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility=View.GONE
            binding.mypageAlertServiceOffIv.visibility=View.VISIBLE
        }
        //서비스 알림 수신 토글 켜기
        binding.mypageAlertServiceOffIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility=View.VISIBLE
            binding.mypageAlertServiceOffIv.visibility=View.GONE
        }
        //마케팅 알림 수신 토글 끄기
        binding.mypageAlertBenefitOnIv.setOnClickListener{
            service.notificationAgreementService(false)
        }
        //마케팅 알림 수신 토글 켜기
        binding.mypageAlertBenefitOffIv.setOnClickListener{
            service.notificationAgreementService(true)
        }
        //이용약관 및 정책
        binding.mypagePolicyIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePolicyFragment())
                .commitAllowingStateLoss()
        }



    }

    /*프로필 사진 재설정 드로다운 메뉴*/
    private fun showDropdown(view : View) {

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

    //마케팅 수신 동의하는 경우 팝업 메시지
    private fun alertAgreementPopup(view: Int){
        val dialog = Dialog(context as MainActivity)
        val today = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
        dialog.setContentView(view)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
        //팝업 바깥 부분 터치하는 경우 팝업 끄기
        dialog.setCanceledOnTouchOutside(true)
        //닉네임, 오늘 날짜 출력하기
        dialog.findViewById<TextView>(R.id.popup_benefit_explain_tv).text = getString(
            R.string.popup_benefit_explain,
            prefs.getString("nickName","null"),
            today)
        //확인버틍으로 팝업 끄기
        dialog.findViewById<TextView>(R.id.popup_benefit_ok_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun successServiceSetting(condition: Boolean) {
        if (condition){ //condition==true: 토글 켜기
            binding.mypageAlertBenefitOnIv.visibility = View.VISIBLE
            binding.mypageAlertBenefitOffIv.visibility = View.GONE
            alertAgreementPopup(R.layout.popup_benefit_agree)
        }else{ //condition == false: 토글 끄기
            binding.mypageAlertBenefitOnIv.visibility = View.GONE
            binding.mypageAlertBenefitOffIv.visibility = View.VISIBLE
        }
    }

    override fun failServiceSetting(message: String) {
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