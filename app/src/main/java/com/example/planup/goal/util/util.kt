package com.example.planup.goal.util

import android.R.attr.data
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.data.GoalCreateRequest
import kotlin.jvm.JvmName
import com.example.planup.main.goal.data.GoalType
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.FriendGoalListResult
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.home.adapter.FriendGoalWithAchievement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit
import java.util.Collections.frequency
import kotlin.collections.map

/** 서버 DTO → 화면용 GoalItem으로 변환 */
@JvmName("myGoalListDtoToGoalItems") // JVM name for the first function
fun List<MyGoalListDto>.toGoalItems(): List<GoalItem> =
    map { dto ->
        val typeLabel = dto.goalType.toCriteria()
        val criteria = "$typeLabel ${dto.frequency}번 이상"

        GoalItem(
            goalId     = dto.goalId,
            title      = dto.goalName.orEmpty(),
            description= criteria,
            percent    = 0,
            authType   = when (dto.goalType) {
                GoalType.CHALLENGE_PHOTO -> "camera"
                GoalType.CHALLENGE_TIME  -> "timer"
                else -> "none"
            },
            isEditMode = false,
            isActive = !dto.isActive,
            criteria   = criteria,
            progress   = 0
        )
    }

/** 서버 DTO → 화면용 GoalItem으로 변환 */
@JvmName("myGoalListItemToGoalItems") // A different JVM name for the second function
fun List<MyGoalListItem>.toGoalItems(): List<GoalItem> =
    map { dto ->
        val typeLabel = dto.goalType.toCriteria()
        val criteria = "$typeLabel ${dto.frequency}번 이상"

        GoalItem(
            goalId     = dto.goalId,
            title      = dto.goalName.orEmpty(),
            description= criteria,
            percent    = 0,
            authType   = when (dto.goalType) {
                "매일" -> "camera"
                "매주"  -> "timer"
                else -> "none"
            },
            isEditMode = false,
            isActive = true,
            criteria   = criteria,
            progress   = 0
        )
    }

// ⬇️ 새로 추가: 친구 리스트 변환
@JvmName("friendGoalListResultToGoalItems")
fun List<FriendGoalListResult>.toGoalItemsForFriend(): List<GoalItem> =
    map { dto ->
        val typeLabel = dto.goalType.toCriteria()
        val criteria = "$typeLabel ${dto.frequency}번 이상"
        GoalItem(
            goalId      = dto.goalId,
            title       = dto.goalName,
            description = criteria,
            percent     = 0,           // 친구용 응답에 퍼센트가 없으므로 0으로 표기 (있으면 반영)
            authType    = dto.verificationType,
            isEditMode  = false,
            isActive    = true,        // 친구 데이터에 공개/활성 여부가 없으니 true로 표시 (필요 시 서버 필드 연결)
            criteria    = criteria,
            progress    = 0            // 필요 시 서버 값 매핑
        )
    }

@JvmName("friendGoalWithAchievementToGoalItems")
fun List<FriendGoalWithAchievement>.toGoalItemsForFriendAchieve(): List<GoalItem> =
    map { dto ->
        val typeLabel = dto.goalType.toCriteria()
        val criteria = "$typeLabel ${dto.frequency}번 이상"
        GoalItem(
            goalId      = dto.goalId,
            title       = dto.goalName,
            description = criteria,
            percent     = 0,           // 친구용 응답에 퍼센트가 없으므로 0으로 표기 (있으면 반영)
            authType    = dto.verificationType,
            isEditMode  = false,
            isActive    = true,        // 친구 데이터에 공개/활성 여부가 없으니 true로 표시 (필요 시 서버 필드 연결)
            criteria    = criteria,
            progress    = 0            // 필요 시 서버 값 매핑
        )
    }

fun String.toCriteria() = when(this){
    GoalType.FRIEND.name -> "매일"
    GoalType.COMMUNITY.name -> "매주"
    GoalType.CHALLENGE_PHOTO.name -> "매일"
    GoalType.CHALLENGE_TIME.name -> "매주"
    else -> "매일"
}
fun GoalType.toCriteria() = when(this){
    GoalType.FRIEND -> "매일"
    GoalType.COMMUNITY -> "매주"
    GoalType.CHALLENGE_PHOTO -> "매일"
    GoalType.CHALLENGE_TIME -> "매주"
}

fun Fragment.setGoalData(data: EditGoalResponse){
    val activity = requireActivity() as GoalActivity
    activity.apply {
            goalName = data.goalName
            goalAmount = data.goalAmount
            goalCategory = data.goalCategory
            goalType = data.goalType
            oneDose = data.oneDose.toString()
            frequency = data.frequency
            period = data.period
            endDate = data.endDate
            verificationType = data.verificationType
            limitFriendCount = data.limitFriendCount
            goalTime = data.goalTime
    }
}

fun setInsets(view: View) {
    setOnApplyWindowInsetsListener(view) { view, insets ->

        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        (view.layoutParams as ViewGroup.MarginLayoutParams).apply {
            bottomMargin = 10 + systemBars.bottom
            view.layoutParams = this
        }
        insets
    }
}

fun Fragment.logGoalActivityData(){
    val activity = requireActivity() as GoalActivity

    activity.apply {
        Log.d("GoalActivity", "{ 카테고리 $goalCategory, $goalType \n" +
                "목표 $goalName ,$goalAmount \n" +
                "인증 방식 $verificationType \n" +
                "투자 시간 $goalTime \n" +
                "$oneDose \n" +
                "세부 목표 $period, $frequency ,$endDate \n" +
                "참여자 제한 $limitFriendCount}")
        Log.d("GoalActivity", "isFriendTab: $isFriendTab")
    }
}

fun Fragment.titleFormat(isFriend: Boolean, isFriendDataTrue: Boolean, titleText: TextView, friendNickname: String, notAction: ()-> Unit){
    titleText.text =
    if(isFriend) {
        if (isFriendDataTrue) {
            getString(R.string.goal_friend_detail, friendNickname)
        }else{
            getString(R.string.goal_friend_detail_title)
        }

    }else{
        notAction()
        getString(R.string.goal_community_detail)
    }
}

fun Fragment.backStackTrueGoalNav(nextFragment: Fragment,name: String?=null){
    Log.d("FRAGMENTS", "${parentFragmentManager.fragments}")
    backStackTrueNav(R.id.goal_container, nextFragment,name)
}

fun Fragment.backStackTrueNav(resId: Int, nextFragment: Fragment,name: String?=null){
    Log.d("FRAGMENTS", "${parentFragmentManager.fragments}")
    parentFragmentManager.beginTransaction()
        .add(resId, nextFragment)
        .hide(this@backStackTrueNav)
        .addToBackStack(name)
        .commitAllowingStateLoss()
}

fun GoalCreateRequest.equil(data: EditGoalResponse): Boolean{
    this.apply {
        return goalName == data.goalName
                && goalAmount == data.goalAmount
                &&goalCategory == data.goalCategory
                &&goalType == data.goalType
                &&oneDose == data.oneDose
                &&frequency == data.frequency
                &&period == data.period
                &&endDate == data.endDate
                &&verificationType == data.verificationType
                &&limitFriendCount == data.limitFriendCount
                &&goalTime == data.goalTime
    }
}

fun EditGoalResponse.equil(data: EditGoalResponse): Boolean{
    this.apply {
        return goalName == data.goalName
                && goalAmount == data.goalAmount
                &&goalCategory == data.goalCategory
                &&goalType == data.goalType
                &&oneDose == data.oneDose
                &&frequency == data.frequency
                &&period == data.period
                &&endDate == data.endDate
                &&verificationType == data.verificationType
                &&limitFriendCount == data.limitFriendCount
                &&goalTime == data.goalTime
    }
}

fun Fragment.goalDataTrue(data: EditGoalResponse): Boolean{
    val activity = requireActivity() as GoalActivity
    activity.apply {
        return goalName == data.goalName
                && goalAmount == data.goalAmount
                &&goalCategory == data.goalCategory
                &&goalType == data.goalType
                &&oneDose == data.oneDose.toString()
                &&frequency == data.frequency
                &&period == data.period
                &&endDate == data.endDate
                &&verificationType == data.verificationType
                &&limitFriendCount == data.limitFriendCount
                &&goalTime == data.goalTime
    }

}

fun Fragment.resetGoalDataTrueCategory(){
    val activity = requireActivity() as GoalActivity
    activity.apply {
        goalName = ""
        goalAmount = ""
        oneDose = ""
        frequency = 0
        period = ""
        endDate = ""
        verificationType = ""
        limitFriendCount = 0
        goalTime = 0
    }
}

fun Int.clockString() = if (this / 10 == 0) {
    "0$this"
} else {
    this.toString()
}

fun Fragment.titleFormat(mode: String, titleText: TextView, friendNickname: String, notAction: ()-> Unit){
    when (mode) {
        "select" -> {
            val title = getString(R.string.goal_select_title)
            titleText.text = title
        }
        "common" -> {
            val title = getString(R.string.goal_select_together)
            titleText.text = title
        }
        "friend" -> {
            val title = getString(R.string.goal_friend_detail, friendNickname)
            titleText.text = title
        }
        else -> {
            val title = getString(R.string.goal_community_detail)
            titleText.text = title
            notAction()
        }
    }
}


fun daysFromToday(dateStr: String): Int {
    val DATE_FORMATTER  = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT)
    val targetDate = runCatching {
        LocalDate.parse(dateStr, DATE_FORMATTER)
    }.getOrNull()
    if (targetDate == null) return -1
    val today = LocalDate.now()

    return ChronoUnit.DAYS.between(today, targetDate).toInt()
}

fun endDateFromToday(days: Int): String {
    return LocalDate.now()
        .plusDays(days.toLong())
        .toString() // yyyy-MM-dd
}

fun goalType(isFriendTab: Boolean) = when (isFriendTab){
    true -> "FRIEND"
    false -> "COMMUNITY"
}

fun setLockText(titleText: TextView, descriptionText: TextView, level: Int){
    titleText.text = "${level+1}번째 목표를 입력하려면?"
    when(level){
        1-> {
            descriptionText.text = "7일간 달성률을 50%이상 유지하세요"
        }
        2-> {
            descriptionText.text = "7일간 매일 1회이상 기록을 남기세요"
        }
        3-> {
            descriptionText.text = "7일간 2개이상의 목표 달성률을 50%이상 유지하세요"
        }
        4 -> {
            descriptionText.text = "새 목표를 추가하고, 7일간 2개 이상의 목표 달성률을 60%이상 유지하세요"
        }
        5 -> {
            descriptionText.text = "7일간 전체 목표 달성률을 50%이상 유지하세요"
        }
        6 -> {
            descriptionText.text = "2주간 2개이상의 목표 달성률을 50%이상 유지하세요"
        }
        7 -> {
            descriptionText.text = "2주간 전체 목표 달성률을 50%이상 유지하세요"
        }
        8 -> {
            descriptionText.text = "새 목표를 추가하고, 7일간 2개 이상의 목표 달성률을 60%이상 유지하세요"
        }
        9 -> {
            descriptionText.text = "3주간 3개 이상의 목표 달성률을 50%이상 유지하세요"
        }
        else -> {}
    }
}

data class TmpGoalData(
    val goalName: String = "",
    val goalAmount: String = "",
    val goalCategory: String = "",
    val goalType: String = "",
    val oneDose: Int = 0,
    val frequency: Int = 0,
    val period: String = "",
    val endDate: String = "",
    val verificationType: String = "",
    val limitFriendCount: Int = 0,
    val goalTime: Int = 0,
    val goalOwnerName:String = ""
)