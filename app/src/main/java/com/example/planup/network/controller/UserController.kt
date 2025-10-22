package com.example.planup.network.controller

import android.util.Log
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.main.my.adapter.CloseAccountAdapter
import com.example.planup.main.my.adapter.EmailLinkAdapter
import com.example.planup.main.my.adapter.SignupLinkAdapter
import com.example.planup.main.my.adapter.KakaoAdapter
import com.example.planup.main.my.adapter.LogoutAdapter
import com.example.planup.main.my.adapter.NicknameChangeAdapter
import com.example.planup.main.my.adapter.PasswordChangeAdapter
import com.example.planup.main.my.adapter.PasswordLinkAdapter
import com.example.planup.main.my.adapter.ProfileImageAdapter
import com.example.planup.network.adapter.KakaoLinkAdapter
import com.example.planup.network.data.EmailLink
import com.example.planup.network.data.KakaoLink
import com.example.planup.network.data.SignupLink
import com.example.planup.network.data.UsingKakao
import com.example.planup.network.data.PasswordLink
import com.example.planup.network.data.ProfileImage
import com.example.planup.network.data.UserResponse
import com.example.planup.network.data.WithDraw
import com.example.planup.network.dto.user.ChangePassword
import com.example.planup.network.dto.user.EmailForPassword
import com.example.planup.network.dto.user.KakaoLinkCode
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.UserPort
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserController {

    /*
* Adapter는 각 API 서비스 응답에 대한 레이아웃 변화를 관리함
* 레이아웃 관리하는 .kt 파일에서 해당 인터페이스를 구현하여 API 응답 반영함*/

    //닉네임 변경
    private lateinit var nicknameChangeAdapter: NicknameChangeAdapter
    fun setNicknameChangeAdapter(adapter: NicknameChangeAdapter) {
        this.nicknameChangeAdapter = adapter
    }

    //회원 탈퇴
    private lateinit var closeAccountAdapter: CloseAccountAdapter
    fun setCloseAccountAdapter(adapter: CloseAccountAdapter) {
        this.closeAccountAdapter = adapter
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
    fun setPasswordLinkAdapter(adapter: PasswordLinkAdapter) {
        this.passwordLinkAdapter = adapter
    }

    //비밀번호 변경
    private lateinit var passwordChangeAdapter: PasswordChangeAdapter
    fun setPasswordChangeAdapter(adapter: PasswordChangeAdapter) {
        this.passwordChangeAdapter = adapter
    }

    //이메일 변경 시 이메일 인증링크 발송
    private lateinit var emailLinkAdapter: EmailLinkAdapter
    fun setEmailLinkAdapter(adapter: EmailLinkAdapter) {
        emailLinkAdapter = adapter
    }

    private lateinit var profileImageAdapter: ProfileImageAdapter
    fun setProfileImageAdapter(adapter: ProfileImageAdapter) {
        profileImageAdapter = adapter
    }

    private lateinit var kakaoLinkAdapter: KakaoLinkAdapter
    fun setKakaoLinkAdapter(adapter: KakaoLinkAdapter) {
        this.kakaoLinkAdapter = adapter
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
    fun passwordLinkService(email: EmailForPassword) {
        val service = getRetrofit().create(UserPort::class.java)
        service.passwordLink(email).enqueue(object : Callback<UserResponse<PasswordLink>> {
            override fun onResponse(
                call: Call<UserResponse<PasswordLink>>,
                response: Response<UserResponse<PasswordLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    passwordLinkAdapter.successPasswordLink(response.body()!!.result.token)
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
    fun passwordRelinkService(email: String) {
        val service = getRetrofit().create(UserPort::class.java)
        service.passwordRelink(email).enqueue(object : Callback<UserResponse<PasswordLink>> {
            override fun onResponse(
                call: Call<UserResponse<PasswordLink>>,
                response: Response<UserResponse<PasswordLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
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
    fun passwordUpdateService(password: ChangePassword) {
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
        kakaoService.getKakao().enqueue(object : Callback<UserResponse<UsingKakao>> {
            override fun onResponse(
                call: Call<UserResponse<UsingKakao>>,
                response: Response<UserResponse<UsingKakao>>
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

            override fun onFailure(call: Call<UserResponse<UsingKakao>>, t: Throwable) {
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

    // 로그아웃
    fun logoutService() {
        val logoutService = getRetrofit().create(UserPort::class.java)
        logoutService.logout().enqueue(object : Callback<UserResponse<Boolean>> {
            override fun onResponse(
                call: Call<UserResponse<Boolean>>,
                response: Response<UserResponse<Boolean>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("okhttp", "로그인 액티비티")
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

    //이메일 변경 시 인증링크 발송
    fun emailLinkService(email: String) {
        val service = getRetrofit().create(UserPort::class.java)
        service.emailLink(email).enqueue(object : Callback<UserResponse<EmailLink>> {
            override fun onResponse(
                call: Call<UserResponse<EmailLink>>,
                response: Response<UserResponse<EmailLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    emailLinkAdapter.successEmailLink(response.body()!!.result.email)


                } else if (!response.isSuccessful && response.body() != null) {
                    when (response.body()!!.code) {
                        "USER403" -> emailLinkAdapter.failEmailLink("이미 존재하는 이메일입니다.")
                    }
                    emailLinkAdapter.failEmailLink(response.body()!!.message)
                } else {
                    emailLinkAdapter.failEmailLink("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<EmailLink>>, t: Throwable) {
                emailLinkAdapter.failEmailLink(t.toString())
            }

        })
    }

    //이메일 변경 시 인증링크 재발송
    fun emailRelinkService(email: String) {
        val service = getRetrofit().create(UserPort::class.java)
        service.emailReLink(email).enqueue(object : Callback<UserResponse<EmailLink>> {
            override fun onResponse(
                call: Call<UserResponse<EmailLink>>,
                response: Response<UserResponse<EmailLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    emailLinkAdapter.successEmailLink(response.body()!!.result.email)
                } else if (!response.isSuccessful && response.body() != null) {
                    emailLinkAdapter.failEmailLink(response.message())
                } else {
                    emailLinkAdapter.failEmailLink("null")
                }
            }

            override fun onFailure(call: Call<UserResponse<EmailLink>>, t: Throwable) {
                emailLinkAdapter.failEmailLink(t.toString())
            }

        })
    }

    //프로필 사진 업로드
    fun imageUploadService(file: MultipartBody.Part) {
        val service = getRetrofit().create(UserPort::class.java)
        service.setProfileImage(file).enqueue(object : Callback<UserResponse<ProfileImage>> {
            override fun onResponse(
                call: Call<UserResponse<ProfileImage>>,
                response: Response<UserResponse<ProfileImage>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    when (response.body()!!.code) {
                        "200" -> profileImageAdapter.successProfileImage(response.body()!!.result.file)
                        "U001" -> profileImageAdapter.failProfileImage(response.body()!!.message)
                    }
                } else if (!response.isSuccessful && response.body() != null) {
                    Log.d("okhttp", "image, ${response.body()!!.message}")
                } else {
                    Log.d("okhttp", "image, null")
                }
            }

            override fun onFailure(call: Call<UserResponse<ProfileImage>>, t: Throwable) {
                profileImageAdapter.failProfileImage(t.toString())
            }

        })
    }


    fun kakaoLinkService(code: String){
        val service = getRetrofit().create(UserPort::class.java)
        service.linkKakao(KakaoLinkCode(code)).enqueue(object : Callback<UserResponse<KakaoLink>>{
            override fun onResponse(
                call: Call<UserResponse<KakaoLink>>,
                response: Response<UserResponse<KakaoLink>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    kakaoLinkAdapter.successKakaoLink(response.body()!!.result.kakaoEmail)
                } else if (!response.isSuccessful && response.body() != null) {
                    when (response.body()!!.code) {
                        "S001" -> kakaoLinkAdapter.failKakaoLink("잘못된 입력값입니다.")
                        "S002" -> kakaoLinkAdapter.failKakaoLink("서버 에러가 발생했습니다")
                        "U001" -> kakaoLinkAdapter.failKakaoLink("존재하지 않는 사용자입니다.")
                        else -> kakaoLinkAdapter.failKakaoLink(response.body()!!.code)
                    }
                } else {
                    kakaoLinkAdapter.failKakaoLink("null")
                }
            }
            override fun onFailure(call: Call<UserResponse<KakaoLink>>, t: Throwable) {
                kakaoLinkAdapter.failKakaoLink(t.toString())
            }

        })
    }
//    //카카오 소셜 로그인
//    fun kakaoSyncronizeService(code: String) {
//        val service = getRetrofit().create(UserPort::class.java)
//        service.syncKakao(code).enqueue(object : Callback<SyncKakao> {
//            override fun onResponse(call: Call<SyncKakao>, response: Response<SyncKakao>) {
//                if (response.isSuccessful && response.body() != null) {
//                    kakaoSyncAdapter.successKakaoSync(response.body()!!.result.userInfo.email)
//                } else if (!response.isSuccessful && response.body() != null) {
//                    when (response.body()!!.code) {
//                        "S001" -> kakaoSyncAdapter.failKakaoSync("잘못된 입력값입니다.")
//                        "S002" -> kakaoSyncAdapter.failKakaoSync("서버 에러가 발생했습니다")
//                        "U001" -> kakaoSyncAdapter.failKakaoSync("존재하지 않는 사용자입니다.")
//                        else -> kakaoSyncAdapter.failKakaoSync(response.body()!!.code)
//                    }
//                } else kakaoSyncAdapter.failKakaoSync("서버로부터 응답이 없습니다.")
//            }
//
//            override fun onFailure(call: Call<SyncKakao>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
}