package com.example.planup.goal.domain

import kotlin.jvm.JvmName
import com.example.planup.main.goal.data.GoalType
import com.example.planup.main.goal.data.MyGoalListDto
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.MyGoalListItem
import kotlin.collections.map

/** 서버 DTO → 화면용 GoalItem으로 변환 */
@JvmName("myGoalListDtoToGoalItems") // JVM name for the first function
fun List<MyGoalListDto>.toGoalItems(): List<GoalItem> =
    map { dto ->
        val typeLabel = when (dto.goalType) {
            GoalType.FRIEND -> "매일"
            GoalType.COMMUNITY -> "매주"
            GoalType.CHALLENGE_PHOTO -> "매일"
            GoalType.CHALLENGE_TIME -> "매주"
        }
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
        val criteria = "$dto.goalType ${dto.frequency}번 이상"

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
