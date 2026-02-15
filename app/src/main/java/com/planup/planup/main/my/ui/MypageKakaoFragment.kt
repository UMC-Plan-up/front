package com.planup.planup.main.my.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.planup.planup.main.MainActivity
import com.planup.planup.R
import com.planup.planup.databinding.FragmentMypageKakaoBinding
import com.planup.planup.main.my.adapter.KakaoAdapter
import com.planup.planup.network.adapter.KakaoLinkAdapter
import com.planup.planup.network.controller.UserController
import com.kakao.sdk.auth.AuthCodeClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MypageKakaoFragment : Fragment(), KakaoAdapter, KakaoLinkAdapter {

    lateinit var binding: FragmentMypageKakaoBinding
    lateinit var prefs: SharedPreferences
    private lateinit var service: UserController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageKakaoBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    private fun init() {
        binding.mypageKakaoCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.mypageKakaoCl.height
                binding.mypageKakaoInnerCl.minHeight = height
                binding.mypageKakaoCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
        service = UserController()
        service.setKakaoAdapter(this)
        service.setKakaoLinkAdapter(this)
        service.kakaoService()
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.kakaoBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun successKakao(kakaoAddr: String?) {
        if (kakaoAddr.isNullOrEmpty()){
            //카카오톡 연동 페이지로 이동
            kakaoLogin()
        } else {
            //연동된 카카오 이메일 계정 전달
            binding.kakaoTitleTv.text = resources.getText(R.string.kakao_sync, kakaoAddr)
        }
    }

    override fun failKakao(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    // 카카오 인가코드 얻기
    private suspend fun getKakaoAuthorizationCode(): String =
        suspendCancellableCoroutine { cont ->
            val callback: (String?, Throwable?) -> Unit = { code, error ->
                if (error != null) cont.resumeWithException(error)
                else cont.resume(code ?: "")
            }

            val client = AuthCodeClient.instance
            if (client.isKakaoTalkLoginAvailable(context as MainActivity)) {
                client.authorizeWithKakaoTalk(context as MainActivity, callback = callback)
            } else {
                client.authorizeWithKakaoAccount(context as MainActivity, callback = callback)
            }
        }
    private fun kakaoLogin(){
        lifecycleScope.launch {
            val code = getKakaoAuthorizationCode()
            service.kakaoLinkService(code.removeSurrounding("\""))
        }
    }

    override fun successKakaoLink(email: String) {
        binding.kakaoExplainFirstTv.text = getString(R.string.kakao_sync,email)
    }

    override fun failKakaoLink(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }
//    private fun kakaoLogin(){
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context as MainActivity)) {
//            // 액세스 토큰 정보 조회
//            UserApiClient.instance.loginWithKakaoTalk(context as MainActivity) { token, error ->
//                Log.e("asdfdasfdsafdsfdsa", "$$token")
//                if (error != null) {
//                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
//
//                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
//                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
//                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
//                        return@loginWithKakaoTalk
//                    }
//
//                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
//                    UserApiClient.instance.loginWithKakaoAccount(context as MainActivity, callback = callback)
//                } else if (token != null) {
//                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
//                }
//            }
//        } else {
//            UserApiClient.instance.loginWithKakaoAccount(context as MainActivity, callback = callback)
//        }
//    }
}