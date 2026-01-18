package com.example.planup.goal.domain

import kotlin.jvm.JvmName
import com.example.planup.main.goal.data.GoalType
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.FriendGoalListResult
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.home.ui.FriendGoalWithAchievement
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

fun List<FriendGoalWithAchievement>.toGoalItemsForFriend(): List<GoalItem> =
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