package com.example.planup.database

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.planup.network.data.UserInfo

/**
 * 사용자 정보 관련 저장
 */
class UserInfoSaver(
    context: Context
) {
    companion object {
        private const val PREF_TOKEN_NAME = "userInfo"

        private const val KEY_ID = "id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_BIRTH_DATE = "birthDate"
        private const val KEY_GENDER = "gender"
        private const val KEY_PROFILE_IMAGE = "profileImg"
        private const val KEY_INVITE_CODE = "invite"


        /**
         * 서비스 알림 수신 여부
         */
        private const val KEY_NOTIFICATION_SERVICE = "notificationService"

        /**
         * 마케팅 정보 수신 여부
         */
        private const val KEY_NOTIFICATION_MARKETING = "notificationMarketing"
    }

    private val prefs = context.getSharedPreferences(PREF_TOKEN_NAME, MODE_PRIVATE)

    // 저장된 유저 정보가 없는지 확인
    val isEmpty
        get() = !prefs.contains(KEY_EMAIL) && !prefs.contains(KEY_NICKNAME) && !prefs.contains(KEY_PROFILE_IMAGE)

    /**
     * 현재 저장된 닉네임을 가져옵니다.
     */
    fun getNickName(): String {
        return prefs.getString(KEY_NICKNAME, "") ?: ""
    }

    /**
     * 닉네임을 저장합니다.
     */
    fun saveNickName(nickName: String) {
        prefs.edit {
            putString(KEY_NICKNAME, nickName)
        }
    }

    /**
     * 현재 저장된 초대 코드를 가져옵니다.
     */
    fun getInviteCode(): String? {
        return prefs.getString(KEY_INVITE_CODE, null)
    }

    /**
     * 초대 코드를 저장합니다.
     */
    fun saveInviteCode(inviteCode: String) {
        prefs.edit {
            putString(KEY_INVITE_CODE, inviteCode)
        }
    }

    /**
     * 이메일을 가져옵니다.
     *
     * @return 저장된 이메일 값
     */
    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, null) ?: ""
    }

    /**
     * 이메일을 저장합니다.
     *
     * @param email 저장할 값
     */
    fun saveEmail(email: String) {
        prefs.edit {
            putString(KEY_EMAIL, email)
        }
    }

    /**
     * 프로필 이미지 주소를 가져옵니다.
     *
     * @return 프로필 이미지 주소
     */
    fun getProfileImage(): String {
        return prefs.getString(KEY_PROFILE_IMAGE, null) ?: ""
    }

    /**
     * 프로필 이미지 주소를 저장합니다.
     *
     * @param profileImage 프로필 이미지 주소
     */
    fun saveProfileImage(profileImage: String?) {
        prefs.edit {
            putString(KEY_PROFILE_IMAGE, profileImage)
        }
    }

    /**
     * 서비스 알림 수신 여부를 가져옵니다.
     **/
    fun getNotificationService(): Boolean {
        //해당 정보는 로컬별로 수신이므로, 기본값은 false
        return prefs.getBoolean(KEY_NOTIFICATION_SERVICE, false)
    }

    /**
     * 서비스 알림 수신여부를 변경합니다.
     */
    fun saveNotificationService(
        notificationService: Boolean
    ) {
        prefs.edit {
            putBoolean(KEY_NOTIFICATION_SERVICE, notificationService)
        }
    }

    /**
     * 혜택 및 마케팅 알림 수신 여부를 가져옵니다.
     **/
    fun getNotificationMarketing(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATION_MARKETING, false)
    }

    /**
     * 혜택 및 마케팅 알림 수신여부를 변경합니다.
     */
    fun saveNotificationMarketing(
        notificationMarketing: Boolean
    ) {
        prefs.edit {
            putBoolean(KEY_NOTIFICATION_MARKETING, notificationMarketing)
        }
    }

    /**
     * 저장된 모든 유저 정보를 지웁니다.
     * (용도) - 로그아웃시
     */
    fun clearAllUserInfo() {
        prefs.edit {
            remove(KEY_ID)
            remove(KEY_EMAIL)
            remove(KEY_NICKNAME)
            remove(KEY_BIRTH_DATE)
            remove(KEY_GENDER)
            remove(KEY_INVITE_CODE)
            remove(KEY_PROFILE_IMAGE)
            remove(KEY_NOTIFICATION_SERVICE)
            remove(KEY_NOTIFICATION_MARKETING)
        }
    }

    fun saveUserInfo(
        userInfo: UserInfo
    ) {
        prefs.edit {
            userInfo.id?.let { putInt(KEY_ID, it) }
            userInfo.email?.let { putString(KEY_EMAIL, it) }
            userInfo.name?.let { putString(KEY_NAME, it) }
            userInfo.nickname?.let { putString(KEY_NICKNAME, it) }
            userInfo.birthDate?.let { putString(KEY_BIRTH_DATE, it) }
            userInfo.gender?.let { putString(KEY_GENDER, it) }
            userInfo.profileImg?.let { putString(KEY_PROFILE_IMAGE, it) }
            userInfo.serviceNotification.let { putBoolean(KEY_NOTIFICATION_SERVICE, it) }
            userInfo.marketingNotification.let { putBoolean(KEY_NOTIFICATION_MARKETING, it) }
        }
    }

    fun toUserInfo(): UserInfo =
        UserInfo(
            id = prefs.getInt(KEY_ID, -1),
            email = prefs.getString(KEY_EMAIL, null),
            name = prefs.getString(KEY_NAME, null),
            nickname = prefs.getString(KEY_NICKNAME, null),
            birthDate = prefs.getString(KEY_BIRTH_DATE, null),
            gender = prefs.getString(KEY_GENDER, null),
            profileImg = prefs.getString(KEY_PROFILE_IMAGE, null),
            serviceNotification = prefs.getBoolean(KEY_NOTIFICATION_SERVICE, false),
            marketingNotification = prefs.getBoolean(KEY_NOTIFICATION_MARKETING, false)
        )
}