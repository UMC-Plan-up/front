package com.example.planup.main.record.data

import com.example.planup.R
import com.example.planup.main.record.adapter.BadgeRow

object BadgeMapper {

    fun createAllBadges(): List<BadgeRow> = buildList {

        /* ===================== 확산 배지 ===================== */
        add(BadgeRow.Header("확산 배지"))
        add(
            BadgeRow.Item(
                badgeType = "INFLUENTIAL_STARTER",
                title = "영향력 있는 시작",
                lockedImageRes = R.drawable.img_badge_leaf_blocked,
                unlockedImageRes = R.drawable.img_badge_leaf
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "WORD_OF_MOUTH_MASTER",
                title = "입소문 장인",
                lockedImageRes = R.drawable.img_badge_leaf_blocked,
                unlockedImageRes = R.drawable.img_badge_leaf
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "MAGNET_USER",
                title = "자석 유저",
                lockedImageRes = R.drawable.img_badge_leaf_blocked,
                unlockedImageRes = R.drawable.img_badge_leaf
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "FRIENDLY_MAX",
                title = "친화력 만랩",
                lockedImageRes = R.drawable.img_badge_leaf_blocked,
                unlockedImageRes = R.drawable.img_badge_leaf
            )
        )

        /* ===================== 상호작용 배지 ===================== */
        add(BadgeRow.Header("상호작용 배지"))
        add(
            BadgeRow.Item(
                badgeType = "FIRST_COMMENT",
                title = "대화의 시작",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "FRIEND_REQUEST_KING",
                title = "친구 신청 왕",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "PROFILE_CLICKER",
                title = "교류 도자",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "FEEDBACK_CHAMPION",
                title = "피드백 챔피언",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "COMMENT_FAIRY",
                title = "댓글 요청",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "CHEER_MASTER",
                title = "응원 마스터",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "REACTION_EXPERT",
                title = "반응 전문가",
                lockedImageRes = R.drawable.img_badge_trophy_blocked,
                unlockedImageRes = R.drawable.img_badge_trophy
            )
        )

        /* ===================== 기록 배지 ===================== */
        add(BadgeRow.Header("기록 배지"))
        add(
            BadgeRow.Item(
                badgeType = "START_OF_CHALLENGE",
                title = "도전의 시작",
                lockedImageRes = R.drawable.img_badge_medal_blocked,
                unlockedImageRes = R.drawable.img_badge_medal
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "DILIGENT_TRACKER",
                title = "성실한 발자국",
                lockedImageRes = R.drawable.img_badge_medal_blocked,
                unlockedImageRes = R.drawable.img_badge_medal
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "ROUTINER",
                title = "루티너",
                lockedImageRes = R.drawable.img_badge_medal_blocked,
                unlockedImageRes = R.drawable.img_badge_medal
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "IMMERSION_DAY",
                title = "몰입의 날",
                lockedImageRes = R.drawable.img_badge_medal_blocked,
                unlockedImageRes = R.drawable.img_badge_medal
            )
        )

        /* ===================== 사용 배지 ===================== */
        add(BadgeRow.Header("사용 배지"))
        add(
            BadgeRow.Item(
                badgeType = "GOAL_COLLECTOR",
                title = "목표 수집가",
                lockedImageRes = R.drawable.img_badge_star_blocked,
                unlockedImageRes = R.drawable.img_badge_star
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "NOTIFICATION_STARTER",
                title = "알림 개시",
                lockedImageRes = R.drawable.img_badge_star_blocked,
                unlockedImageRes = R.drawable.img_badge_star
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "ANALYST",
                title = "분석가",
                lockedImageRes = R.drawable.img_badge_star_blocked,
                unlockedImageRes = R.drawable.img_badge_star
            )
        )
        add(
            BadgeRow.Item(
                badgeType = "CONSISTENT_RECORDER",
                title = "꾸준한 기록가",
                lockedImageRes = R.drawable.img_badge_star_blocked,
                unlockedImageRes = R.drawable.img_badge_star
            )
        )
    }
}