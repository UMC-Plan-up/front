package com.example.planup.network.controller

import android.util.Log
import com.example.planup.login.adapter.LoginAdapter
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.main.my.adapter.CloseAccountAdapter
import com.example.planup.main.my.adapter.EmailSendAdapter
import com.example.planup.main.my.adapter.KakaoAdapter
import com.example.planup.main.my.adapter.LogoutAdapter
import com.example.planup.main.my.ui.ResponseViewer
import com.example.planup.network.data.EmailSend
import com.example.planup.network.data.KakaoAccount
import com.example.planup.network.data.Login
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

    //서버 응답에 따른 마이페이지 관련레이아웃 관리를 위한 변수
    private lateinit var responseViewer: ResponseViewer
    fun setResponseViewer(viewer: ResponseViewer) {
        this.responseViewer = viewer
    }

    //회원 탈퇴
    private lateinit var closeAccountAdapter: CloseAccountAdapter
    fun setCloseAccountAdapter(adapter: CloseAccountAdapter) {
        this.closeAccountAdapter = adapter
    }

    //로그인에
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

    //이메일 인증링크 발송
    private lateinit var emailSendAdapter: EmailSendAdapter
    fun setEmailSendAdapter(adapter: EmailSendAdapter) {
        this.emailSendAdapter = adapter
    }

    //새로운 nickname으로 수정
    fun nicknameService(userId: Int, nickname: String) {
        val nicknameService = getRetrofit().create(UserPort::class.java)
        nicknameService.changeNickname(userId, nickname)
            .enqueue(object : Callback<UserResponse<String>> {
                override fun onResponse(
                    call: Call<UserResponse<String>>,
                    response: Response<UserResponse<String>>
                ) {
                    if (response.isSuccessful && response.body()?.result != null) {
                        responseViewer.onResponseSuccess()
                    } else {
                        responseViewer.onResponseError(
                            response.code().toString(),
                            response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse<String>>, t: Throwable) {
                    Log.d("okhttp", "fail\n$t")
                }
            })
    }

    //이메일 변경
    fun emailService(userId: Int, email: String) {
        val emailService = getRetrofit().create(UserPort::class.java)
        emailService.changeEmail(userId, email).enqueue(object : Callback<UserResponse<String>> {
            override fun onResponse(
                call: Call<UserResponse<String>>,
                response: Response<UserResponse<String>>
            ) {
                if (response.isSuccessful && response.body()?.result != null) {
                    responseViewer.onResponseSuccess()
                } else {
                    responseViewer.onResponseError(response.code().toString(), response.message())
                }
            }

            override fun onFailure(call: Call<UserResponse<String>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //이메일로 인증링크 요청
    fun emailSendService(email: String) {
        val emailSendService = getRetrofit().create(UserPort::class.java)
        emailSendService.sendEmail(email).enqueue(object : Callback<UserResponse<EmailSend>> {
            override fun onResponse(
                call: Call<UserResponse<EmailSend>>,
                response: Response<UserResponse<EmailSend>>
            ) {
                if (response.isSuccessful && response.body()?.result != null) {
                    response.body()!!.result.email.let { emailSendAdapter.successEmailSend(it) }
                } else {
                    Log.d("okhttp", "error ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse<EmailSend>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //이메일 다시 받기
    fun emailResendService(email: String) {
        val emailResendService = getRetrofit().create(UserPort::class.java)
        emailResendService.resendEmail(email).enqueue(object : Callback<UserResponse<EmailSend>> {
            override fun onResponse(
                call: Call<UserResponse<EmailSend>>,
                response: Response<UserResponse<EmailSend>>
            ) {
                if (response.isSuccessful && response.body()?.result != null) {
                    response.body()!!.result.email.let { emailSendAdapter.successEmailSend(it) }
                } else {
                    Log.d("okhttp", "error ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse<EmailSend>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //비밀번호 변경
    fun passwordService(userId: Int, password: String) {
        val passwordService = getRetrofit().create(UserPort::class.java)
        passwordService.changePassword(userId, password)
            .enqueue(object : Callback<UserResponse<Boolean>> {
                override fun onResponse(
                    call: Call<UserResponse<Boolean>>,
                    response: Response<UserResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body()?.result == true) {
                        responseViewer.onResponseSuccess()
                    } else {
                        responseViewer.onResponseError(
                            response.code().toString(),
                            response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                    Log.d("okhttp", "fail\n$t")
                }
            })
    }

    //카카오 계정 연동상태 확인
    fun kakaoService() {
        val kakaoService = getRetrofit().create(UserPort::class.java)
        kakaoService.getKakao().enqueue(object : Callback<UserResponse<KakaoAccount>> {
            override fun onResponse(
                call: Call<UserResponse<KakaoAccount>>,
                response: Response<UserResponse<KakaoAccount>>
            ) {
                if (response.isSuccessful && response.body()?.result != null) {
                    val email = response.body()!!.result.kakaoEmail
                    kakaoAdapter.successKakao(if (email.isEmpty()) "@" else email)
                } else {
                    kakaoAdapter.failKakao()
                }
            }

            override fun onFailure(call: Call<UserResponse<KakaoAccount>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //서비스 알림 수신 여부 관리
    fun notificationAgreementService(condition: Boolean) {
        val notificationAgreementService = getRetrofit().create(UserPort::class.java)
        notificationAgreementService.patchNoticeAgree()
            .enqueue(object : Callback<UserResponse<Boolean>> {
                override fun onResponse(
                    call: Call<UserResponse<Boolean>>,
                    response: Response<UserResponse<Boolean>>
                ) {
                    if (response.isSuccessful && response.body()?.result != null) {
                        serviceAdapter.successServiceSetting(condition)
                    } else {
                        Log.d("okhttp", "error ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                    Log.d("okhttp", "fail\n$t")
                }
            })
    }

    //로그인
    fun loginService(loginDto: LoginDto) {
        val loginService = getRetrofit().create(UserPort::class.java)
        loginService.login(loginDto).enqueue(object : Callback<UserResponse<Login>> {
            override fun onResponse(
                call: Call<UserResponse<Login>>,
                response: Response<UserResponse<Login>>
            ) {
                if (response.isSuccessful && response.body()?.result != null) {
                    loginAdapter.successLogin(response.body()!!.result)
                } else {
                    response.body()?.result?.let { loginAdapter.failLogin(it.message) }
                }
            }

            override fun onFailure(call: Call<UserResponse<Login>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //로그아웃
    fun logoutService() {
        val logoutService = getRetrofit().create(UserPort::class.java)
        logoutService.logout().enqueue(object : Callback<UserResponse<Boolean>> {
            override fun onResponse(
                call: Call<UserResponse<Boolean>>,
                response: Response<UserResponse<Boolean>>
            ) {
                if (response.isSuccessful && response.body()?.result == true) {
                    logoutAdapter.successLogout()
                } else {
                    logoutAdapter.failLogout()
                }
            }

            override fun onFailure(call: Call<UserResponse<Boolean>>, t: Throwable) {
                Log.d("okhttp", "fail\n$t")
            }
        })
    }

    //회원 탈퇴
    fun closeAccountService(reason: String) {
        val closeAccountService = getRetrofit().create(UserPort::class.java)
        closeAccountService.withdrawAccount(reason)
            .enqueue(object : Callback<UserResponse<WithDraw>> {
                override fun onResponse(
                    call: Call<UserResponse<WithDraw>>,
                    response: Response<UserResponse<WithDraw>>
                ) {
                    if (response.isSuccessful && response.body()?.result != null) {
                        closeAccountAdapter.successCloseAccount()
                    } else {
                        closeAccountAdapter.failCloseAccount()
                    }
                }

                override fun onFailure(call: Call<UserResponse<WithDraw>>, t: Throwable) {
                    Log.d("okhttp", "fail\n$t")
                }
            })
    }
}