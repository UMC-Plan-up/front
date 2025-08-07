package com.example.planup.network.port

import com.example.planup.main.my.data.GetKakao
import com.example.planup.main.my.data.GetNickname
import com.example.planup.main.my.data.Logout
import com.example.planup.main.my.data.PatchNotificationAgreement
import com.example.planup.main.my.data.PostEmail
import com.example.planup.main.my.data.PostMypageImage
import com.example.planup.main.my.data.PostNickname
import com.example.planup.main.my.data.PostPassword
import com.example.planup.main.my.data.PostPasswordChange
import com.example.planup.main.my.data.PostProfileImage
import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.data.PostFriendsReport
import com.example.planup.network.data.PostFriendsUnblocked
import com.example.planup.network.entity.FriendReportDto
import com.example.planup.network.entity.FriendUnblockDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserControllerInterface {
    @GET("mypage/profile/nickname")
    fun getNickname(@Query("userId") userId:Int):Call<GetNickname>
    @GET("mypage/kakao-account")
    fun getKakao(@Query("userId") userId: Int):Call<GetKakao>

    @PATCH("mypage/notification/agree")
    fun patchNoticeAgree(@Query("userId") userId:Int):Call<PatchNotificationAgreement>

    @POST("users/logout")
    fun logout():Call<Logout>
    @POST("profile/image")
    fun setProfileImage():Call<PostProfileImage>
    @POST("mypage/profile/password")
    fun checkPassword(@Query("userId") userId: Int, @Query("password") password: String): Call<PostPassword>
    @POST("mypage/profile/password/update")
    fun changePassword(@Query("userId") userId: Int, @Query("password") password: String): Call<PostPasswordChange>
    @POST("mypage/profile/nickname")
    fun changeNickname(@Query("userId") userId: Int, @Body nickname: String): Call<PostNickname>
    @POST("mypage/profile/image")
    fun changeProfileImage(@Query("userId") userId: Int, @Body image: String): Call<PostMypageImage>
    @POST("mypage/profile/email")
    fun changeEmail(@Query("userId") userId: Int, @Query("newEmail") email: String): Call<PostEmail>

    @GET("friends/blocked")
    fun blockedFriend(): Call<BlockedFriends>
    @POST("friends/unblocked")
    fun unblockedFriend(@Body friend: FriendUnblockDto): Call<PostFriendsUnblocked>
    @POST("friends/report")
    fun reportFriend(@Body friend: FriendReportDto): Call<PostFriendsReport>
}