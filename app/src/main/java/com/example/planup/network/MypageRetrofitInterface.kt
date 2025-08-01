package com.example.planup.network

import com.example.planup.main.my.data.GetKakao
import com.example.planup.main.my.data.GetNickname
import com.example.planup.main.my.data.GetUserInfo
import com.example.planup.main.my.data.Login
import com.example.planup.main.my.data.Logout
import com.example.planup.main.my.data.PatchNotificationAgreement
import com.example.planup.main.my.data.PostEmail
import com.example.planup.main.my.data.PostMypageImage
import com.example.planup.main.my.data.PostNickname
import com.example.planup.main.my.data.PostPassword
import com.example.planup.main.my.data.PostPasswordChange
import com.example.planup.main.my.data.Signup
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MypageRetrofitInterface {
    @GET("mypage/profile/nickname")
    fun getNickname(userId:Int):Call<GetNickname>
    @GET("users/info")
    fun getUserInfo(userId:Int):Call<GetUserInfo>
    @GET("mypage/kakao-account")
    fun getKakao(userId: Int):Call<GetKakao>

    @PATCH("mypage/notification/agree")
    fun patchNoticeAgree(userId: Int):Call<PatchNotificationAgreement>

    @POST("users/signup")
    fun signUp():Call<Signup>
    @POST("users/logout")
    fun logout():Call<Logout>
    @POST("users/login")
    fun login(): Call<Login>
    @POST("profile/image")
    fun setProfileImage():Call<PostProfileImage>
    @POST("mypage/profile/password")
    fun checkPassword(userId: Int, password:String): Call<PostPassword>
    @POST("mypage/profile/password/update")
    fun changePassword(userId: Int, password:String): Call<PostPasswordChange>
    @POST("mypage/profile/nickname")
    fun changeNickname(userId: Int, nickname:String): Call<PostNickname>
    @POST("mypage/profile/image")
    fun changeProfileImage(userId: Int,image:String): Call<PostMypageImage>
    @POST("mypage/profile/email")
    fun changeEmail(userId: Int,newEmail:String): Call<PostEmail>
}