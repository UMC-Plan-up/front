package com.example.planup.network.controller

import android.util.Log
import com.example.planup.login.adapter.LoginAdapter
import com.example.planup.main.home.adapter.UserInfoAdapter
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.main.my.adapter.CloseAccountAdapter
import com.example.planup.main.my.adapter.SignupLinkAdapter
import com.example.planup.main.my.adapter.KakaoAdapter
import com.example.planup.main.my.adapter.LogoutAdapter
import com.example.planup.main.my.adapter.NicknameChangeAdapter
import com.example.planup.main.my.adapter.PasswordChangeAdapter
import com.example.planup.main.my.adapter.PasswordLinkAdapter
import com.example.planup.network.data.SignupLink
import com.example.planup.network.data.KakaoAccount
import com.example.planup.network.data.Login
import com.example.planup.network.data.PasswordLink
import com.example.planup.network.data.UserInfo
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.WithDraw
import com.example.planup.network.dto.user.LoginDto
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.UserPort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserController {

    /*
* Adapter는 각 API 서비스 응답에 대한 레이아웃 변화를 관리함
* 레이아웃 관리하는 .kt 파일에서 해당 인터페이스를 구현하여 API 응답 반영함*/

    //닉네임 변경
    private lateinit var nicknameChangeAdapter: NicknameChangeAdapter
    fun setNicknameChangeAdapter(adapter: NicknameChangeAdapter){
        this.nicknameChangeAdapter = adapter
    }

    //회원 정보 조회
    private lateinit var userInfoAdapter: UserInfoAdapter
    fun setUserInfoAdapter(adapter: UserInfoAdapter){
        this.userInfoAdapter = adapter
    }
    //회원 탈퇴
    private lateinit var closeAccountAdapter: CloseAccountAdapter
    fun setCloseAccountAdapter(adapter: CloseAccountAdapter) {
        this.closeAccountAdapter = adapter
    }

    //로그인
    private lateinit var loginAdapter: LoginAdapter
    fun setLoginAdapter(adapter: LoginAdapter) {
        this.loginAdapter = adapter
    }

    //로그아웃
    private lateinit var logoutAdapter: LogoutAdapter
    fun setLogoutAdapter(adapter: LogoutAdapter) {
        this.logoutAdapter = adapter
    }

    //카카오톡 연동 상태 확인
    private lateinit var kakaoAdapter: KakaoAdapter
    fun setKakaoAdapter(adapter: KakaoAdapter) {
        this.kakaoAdapter = adapter
    }

    //서비스 알림 동의 변경
    private lateinit var serviceAdapter: ServiceAlertAdapter
    fun setServiceAdapter(adapter: ServiceAlertAdapter) {
        this.serviceAdapter = adapter
    }

    //회원가입 시 이메일 인증링크 발송
    private lateinit var signupLinkAdapter: SignupLinkAdapter
    fun setSignupLinkAdapter(adapter: SignupLinkAdapter) {
        this.signupLinkAdapter = adapter
    }

    //비밀번호 변경 시 이메일 인증링크 발송
    private lateinit var passwordLinkAdapter: PasswordLinkAdapter
    fun setPasswordLinkAdapter(adapter: PasswordLinkAdapter){
        this.passwordLinkAdapter = adapter
    }

    //비밀번호 변경
    private lateinit var passwordChangeAdapter: PasswordChangeAdapter
    fun setPasswordChangeAdapter(adapter: PasswordChangeAdapter){
        this.passwordChangeAdapter = adapter
    }

    //유저 정보 조회
    fun userInfoService(){
        val service = getRetrofit().create(UserPort::class.java)
        service.getUserInfo().enqueue(object : Callback<UserResponse<UserInfo>>{
            override fun onResponse(
                call: Call<UserResponse<UserInfo>>,
                response: Response<UserResponse<UserInfo>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    userInfoAdapter.successUserInfo(response.body()!!.result)
                } else if (!response.isSuccessful && response.body() != null){
                    userInfoAdapter.failUserInfo(response.body()!!.message)
                } else {
                    userInfoAdapter.failUserInfo("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<UserInfo>>, t: Throwable) {
                userInfoAdapter.failUserInfo(t.toString())
            }

        })
    }

    //새로운 nickname으로 수정
    fun nicknameService(nickname: String) {
        val nicknameService = getRetrofit().create(UserPort::class.java)
        nicknameService.changeNickname(nickname)
            .enqueue(object : Callback<UserResponse<String>> {
                override fun onResponse(
                    call: Call<UserResponse<String>>,
                    response: Response<UserResponse<String>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        nicknameChangeAdapter.successNicknameChange()
                    } else if (!response.isSuccessful && response.body() != null) {
                        nicknameChangeAdapter.failNicknameChange(response.body()!!.message)
                    } else {
                        nicknameChangeAdapter.failNicknameChange("null")
                    }
                }

                override fun onFailure(call: Call<UserResponse<String>>, t: Throwable) {
                    nicknameChangeAdapter.failNicknameChange(t.toString())
                }
            })
    }

    //회원가입 시 인증링크 재발송
    fun signupLinkService(email: String) {
        val emailSendService = getRetrofit().create(UserPort::class.java)
        emailSendService.signupLink(email).enqueue(object : Callback<UserResponse<SignupLink>> {
            override fun onResponse(
                call: Call<UserResponse<SignupLink>>,
                response: Response<UserResponse<SignupLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    signupLinkAdapter.successEmailSend(response.body()!!.result.email)
                } else if (!response.isSuccessful && response.body() != null) {
                    signupLinkAdapter.failEmailSend(response.body()!!.message)
                } else {
                    signupLinkAdapter.failEmailSend("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<SignupLink>>, t: Throwable) {
                signupLinkAdapter.failEmailSend(t.toString())
            }
        })
    }

    //회원가입 시 인증링크 발송
    fun signupRelinkService(email: String) {
        val emailResendService = getRetrofit().create(UserPort::class.java)
        emailResendService.signupRelink(email).enqueue(object : Callback<UserResponse<SignupLink>> {
            override fun onResponse(
                call: Call<UserResponse<SignupLink>>,
                response: Response<UserResponse<SignupLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    signupLinkAdapter.successEmailSend(response.body()!!.result.email)
                } else if (!response.isSuccessful && response.body() != null) {
                    signupLinkAdapter.failEmailSend(response.body()!!.message)
                } else {
                    signupLinkAdapter.failEmailSend("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<SignupLink>>, t: Throwable) {
                signupLinkAdapter.failEmailSend(t.toString())
            }
        })
    }

    // 비밀번호 변경 시 인증링크 발송
    fun passwordLinkService(email: String){
        val service = getRetrofit().create(UserPort::class.java)
        service.passwordLink(email).enqueue(object : Callback<UserResponse<PasswordLink>>{
            override fun onResponse(
                call: Call<UserResponse<PasswordLink>>,
                response: Response<UserResponse<PasswordLink>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    passwordLinkAdapter.successPasswordLink(response.body()!!.result.email)
                } else if (!response.isSuccessful && response.body() != null) {
                    passwordLinkAdapter.failPasswordLink(response.body()!!.message)
                } else {
                    passwordLinkAdapter.failPasswordLink("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<PasswordLink>>, t: Throwable) {
                passwordLinkAdapter.failPasswordLink(t.toString())
            }
        })
    }

    // 비밀번호 변경 시 인증링크 재발송
    fun passwordRelinkService(email: String){
        val service = getRetrofit().create(UserPort::class.java)
        service.passwordRelink(email).enqueue(object : Callback<UserResponse<PasswordLink>>{
            override fun onResponse(
                call: Call<UserResponse<PasswordLink>>,
                response: Response<UserResponse<PasswordLink>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    passwordLinkAdapter.successPasswordLink(response.body()!!.result.email)
                } else if (!response.isSuccessful && response.body() != null) {
                    passwordLinkAdapter.failPasswordLink(response.body()!!.message)
                } else {
                    passwordLinkAdapter.failPasswordLink("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<PasswordLink>>, t: Throwable) {
                passwordLinkAdapter.failPasswordLink(t.toString())
            }
        })
    }

    // 비밀번호 변경
    fun passwordUpdateService(password: String) {
        val passwordService = getRetrofit().create(UserPort::class.java)
        passwordService.changePassword(password)
            .enqueue(object : Callback<UserResponse<Boolean>> {
                override fun onResponse(
                    call: Call<UserResponse<Boolean>>,
                    response: Response<UserResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        passwordChangeAdapter.successPasswordChange()
                    } else if (!response.isSuccessful && response.body() != null) {
                        passwordChangeAdapter.failPasswordChange(response.body()!!.message)
                    } else {
                        passwordChangeAdapter.failPasswordChange("null")
                    }
                }

                override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                    passwordChangeAdapter.failPasswordChange(t.toString())
                }
            })
    }

    // 카카오 계정 연동상태 확인
    fun kakaoService() {
        val kakaoService = getRetrofit().create(UserPort::class.java)
        kakaoService.getKakao().enqueue(object : Callback<UserResponse<KakaoAccount>> {
            override fun onResponse(
                call: Call<UserResponse<KakaoAccount>>,
                response: Response<UserResponse<KakaoAccount>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val email = response.body()!!.result.kakaoEmail
                    kakaoAdapter.successKakao(email)
                } else if (!response.isSuccessful && response.body() != null) {
                    kakaoAdapter.failKakao(response.body()!!.message)
                } else {
                    kakaoAdapter.failKakao("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<KakaoAccount>>, t: Throwable) {
                kakaoAdapter.failKakao(t.toString())
            }
        })
    }

    // 서비스 알림 수신 여부 관리
    fun notificationAgreementService(condition: Boolean) {
        val notificationAgreementService = getRetrofit().create(UserPort::class.java)
        notificationAgreementService.patchNoticeAgree()
            .enqueue(object : Callback<UserResponse<Boolean>> {
                override fun onResponse(
                    call: Call<UserResponse<Boolean>>,
                    response: Response<UserResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        serviceAdapter.successServiceSetting(condition)
                    } else if (!response.isSuccessful && response.body() != null) {
                        serviceAdapter.failServiceSetting(response.body()!!.message)
                    } else {
                        serviceAdapter.failServiceSetting("null")
                    }
                }

                override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                    serviceAdapter.failServiceSetting(t.toString())
                }
            })
    }

    // 로그인
    fun loginService(loginDto: LoginDto) {
        val loginService = getRetrofit().create(UserPort::class.java)
        loginService.login(loginDto).enqueue(object : Callback<UserResponse<Login>> {
            override fun onResponse(
                call: Call<UserResponse<Login>>,
                response: Response<UserResponse<Login>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    loginAdapter.successLogin(response.body()!!.result)
                } else if (!response.isSuccessful && response.body() != null) {
                    loginAdapter.failLogin(response.body()!!.message)
                } else {
                    loginAdapter.failLogin("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<Login>>, t: Throwable) {
                loginAdapter.failLogin(t.toString())
            }
        })
    }

    // 로그아웃
    fun logoutService() {
        val logoutService = getRetrofit().create(UserPort::class.java)
        logoutService.logout().enqueue(object : Callback<UserResponse<Boolean>> {
            override fun onResponse(
                call: Call<UserResponse<Boolean>>,
                response: Response<UserResponse<Boolean>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("okhttp","로그인 액티비티")
                    logoutAdapter.successLogout()
                } else if (!response.isSuccessful && response.body() != null) {
                    logoutAdapter.failLogout(response.body()!!.message)
                } else {
                    logoutAdapter.failLogout("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                logoutAdapter.failLogout(t.toString())
            }
        })
    }

    // 회원 탈퇴
    fun closeAccountService(reason: String) {
        val closeAccountService = getRetrofit().create(UserPort::class.java)
        closeAccountService.withdrawAccount(reason)
            .enqueue(object : Callback<UserResponse<WithDraw>> {
                override fun onResponse(
                    call: Call<UserResponse<WithDraw>>,
                    response: Response<UserResponse<WithDraw>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        closeAccountAdapter.successCloseAccount()
                    } else if (!response.isSuccessful && response.body() != null) {
                        closeAccountAdapter.failCloseAccount(response.body()!!.message)
                    } else {
                        closeAccountAdapter.failCloseAccount("null")
                    }
                }

                override fun onFailure(call: Call<UserResponse<WithDraw>>, t: Throwable) {
                   closeAccountAdapter.failCloseAccount(t.toString())
                }
            })
    }
}