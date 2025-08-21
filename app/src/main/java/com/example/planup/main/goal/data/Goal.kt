package com.example.planup.main.goal.data

import com.example.planup.main.goal.item.Comment
import com.example.planup.main.goal.item.TimerVerification
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.user.model.User

data class Goal(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("goalName") val goalName: String?,
    @SerializedName("goalAmount") val goalAmount: String,
    @SerializedName("goalCategory") val goalCategory: GoalCategory,
    @SerializedName("goalType") val goalType: GoalType,
    @SerializedName("period") val period: Period,
    @SerializedName("userGoals") val userGoals: UserGoal,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("oneDose") val oneDose: Int,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("verificationType") val verificationType: VerificationType,
    @SerializedName("limitFriendCount") val limitFriendCount: Int,
    @SerializedName("commentList") val commentList: List<Comment>,
    @SerializedName("active") val active: Boolean,
    @SerializedName("challenge") val challenge: Boolean,
)

enum class GoalCategory{
    @SerializedName("STUDYING") STUDYING,
    @SerializedName("READING") READING,
    @SerializedName("DIGITAL_DETOX") DIGITAL_DETOX,
    @SerializedName("MEDITATION") MEDITATION,
    @SerializedName("SLEEP_PATTERN") SLEEP_PATTERN,
    @SerializedName("EATING") EATING,
    @SerializedName("MUSIC") MUSIC,
    @SerializedName("EXERCISE") EXERCISE,
    @SerializedName("DIARY") DIARY,
    @SerializedName("SELF") SELF,
    @SerializedName("CHALLENGE") CHALLENGE
}

enum class Period{
    @SerializedName("DAY") DAY,
    @SerializedName("WEEK") WEEK,
    @SerializedName("MONTH") MONTH
}

enum class VerificationType{
    @SerializedName("PHOTO") PHOTO,
    @SerializedName("TIMER") TIMER
}

data class UserGoal(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: Status,
    @SerializedName("currentAmount") val currentAmount: String,
    @SerializedName("verificationCount") val verificationCount: Int,
    @SerializedName("goalTime") val goalTime: Int,
    @SerializedName("user") val user: User,
    @SerializedName("timerVerifications") val timerVerifications: TimerVerification,
    @SerializedName("photoVerifications") val photoVerifications: PhotoVerification,
    @SerializedName("public") val public: Boolean,
    @SerializedName("active") val active: Boolean
)

enum class Status{
    @SerializedName("ADMIN") ADMIN,
    @SerializedName("MEMBER") MEMBER
}

data class PhotoVerification(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("photoImg") val photoImg: String
)