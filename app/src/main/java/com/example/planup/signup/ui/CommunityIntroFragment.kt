package com.example.planup.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.main.goal.item.GoalItemAlt
import com.example.planup.signup.SignupActivity
import com.example.planup.databinding.FragmentCommunityIntroBinding
import com.example.planup.databinding.ItemGoalAltBinding

class CommunityIntroFragment : Fragment() {

    private var _binding: FragmentCommunityIntroBinding? = null
    private val binding get() = _binding!!

    // 카테고리별 목표 데이터
    private val goalData = mapOf(
        "공부하기" to listOf(
            GoalItemAlt("매일 2시간 이상 영어 공부하기", R.drawable.ic_book),
            GoalItemAlt("매주 3회 이상 온라인 강의 듣기", R.drawable.ic_laptop),
            GoalItemAlt("매일 30분 이상 문제집 풀기", R.drawable.ic_notebook),
            GoalItemAlt("매주 2회 이상 스터디 모임 참석하기", R.drawable.ic_people)
        ),
        "독서하기" to listOf(
            GoalItemAlt("매일 30분 이상 책 읽기", R.drawable.ic_book2),
            GoalItemAlt("주 3회 이상 독서 노트 작성하기", R.drawable.ic_notebook2),
            GoalItemAlt("매달 1회 이상 책 완독하기", R.drawable.ic_check_green),
            GoalItemAlt("매달 2회 이상 독서 모임 참석하기", R.drawable.ic_coffee2)
        ),
        "명상하기" to listOf(
            GoalItemAlt("매일 10분 이상 명상하기", R.drawable.ic_meditation),
            GoalItemAlt("매주 5회 이상 아침 명상하기", R.drawable.ic_sunrise),
            GoalItemAlt("매일 1회 이상 심호흡 5분 하기", R.drawable.ic_breath),
            GoalItemAlt("매주 1회 이상 명상, 스트레칭 하기", R.drawable.ic_yoga)
        ),
        "수면 패턴 관리" to listOf(
            GoalItemAlt("매주 3회 이상 자정 이전 취침", R.drawable.ic_bed),
            GoalItemAlt("매일 아침 7시 이전 일어나기", R.drawable.ic_clock),
            GoalItemAlt("매일 8시간 이상 수면 취하기", R.drawable.ic_sleep),
            GoalItemAlt("주 5회 이상 자기 전 스마트폰 금지", R.drawable.ic_phone_off)
        ),
        "식습관 관리" to listOf(
            GoalItemAlt("매일 아침 식사 챙겨먹기", R.drawable.ic_salad),
            GoalItemAlt("주 3회 이상 야채 위주 식사", R.drawable.ic_vitamin),
            GoalItemAlt("하루 물 2L 이상 마시기", R.drawable.ic_water),
            GoalItemAlt("주 1회 이상 건강식 요리하기", R.drawable.ic_rice)
        ),
        "악기 배우기" to listOf(
            GoalItemAlt("매일 30분 이상 피아노 연습하기", R.drawable.ic_piano),
            GoalItemAlt("매주 3회 이상 기타 코드 연습하기", R.drawable.ic_guitar),
            GoalItemAlt("매주 1곡 이상 악보 완주하기", R.drawable.ic_sheet),
            GoalItemAlt("매주 1회 이상 드럼 레슨 받기", R.drawable.ic_drum)
        ),
        "운동하기" to listOf(
            GoalItemAlt("주 3회 이상 필라테스 수업 듣기", R.drawable.ic_tennis),
            GoalItemAlt("주 1회 이상 10층 계단 오르기", R.drawable.ic_boxing),
            GoalItemAlt("매일 30분 러닝하기", R.drawable.ic_hockey),
            GoalItemAlt("하루 한 번 이상 헬스장 가기", R.drawable.ic_flag)
        ),
        "일기 쓰기" to listOf(
            GoalItemAlt("매주 3회 이상 모닝 페이지 작성하기", R.drawable.ic_pencil),
            GoalItemAlt("매일 감사 일기 쓰기", R.drawable.ic_note),
            GoalItemAlt("매주 1회 이상 주간 일기 쓰기", R.drawable.ic_calendar),
            GoalItemAlt("매일 감정 일기 쓰기", R.drawable.ic_emotion)
        )
    )

    // 목표 리스트를 갱신하는 함수
    private fun updateGoalList(category: String) {
        binding.goalListContainer.removeAllViews()
        val goals = goalData[category] ?: emptyList()
        goals.forEach { goal ->
            val itemBinding = ItemGoalAltBinding.inflate(layoutInflater, binding.goalListContainer, false)
            itemBinding.goalIcon.setImageResource(goal.iconRes)
            itemBinding.goalText.text = goal.text
            binding.goalListContainer.addView(itemBinding.root)
        }
    }

    // 카테고리 View ID → 카테고리 이름 매핑
    private val categoryMap = mapOf(
        R.id.category_exercise to "운동하기",
        R.id.category_study to "공부하기",
        R.id.category_reading to "독서하기",
        R.id.category_meditation to "명상하기",
        R.id.category_sleeping to "수면 패턴 관리",
        R.id.category_eating to "식습관 관리",
        R.id.category_learning to "악기 배우기",
        R.id.category_writing to "일기 쓰기"
    )

    // 카테고리 클릭 리스너 등록
    private fun setupCategoryClickListeners() {
        categoryMap.forEach { (viewId, categoryName) ->
            binding.root.findViewById<View>(viewId)?.setOnClickListener {
                updateGoalList(categoryName)
            }
        }
    }

    companion object {
        private const val ARG_NICKNAME = "nickname"

        fun newInstance(nickname: String): CommunityIntroFragment {
            val fragment = CommunityIntroFragment()
            val args = Bundle()
            args.putString(ARG_NICKNAME, nickname)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickname = arguments?.getString(ARG_NICKNAME) ?: "사용자"
        binding.titleText.text = getString(R.string.community_greeting, nickname)

        setupCategoryClickListeners()
        updateGoalList("공부하기")

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeInputFragment())
        }

        /* 목표 설정 시작하기 버튼 → GoalCategoryFragment로 이동 */
        binding.nextButton.setOnClickListener {
            val realNickname = (requireActivity() as SignupActivity).nickname ?: "사용자"
            val intent = Intent(requireContext(), GoalActivity::class.java).apply {
                putExtra("goalOwnerName", realNickname)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
