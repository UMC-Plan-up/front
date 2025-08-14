package com.example.planup.main.record.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBadgesBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.record.adapter.BadgeRow
import com.example.planup.main.record.adapter.BadgeSectionAdapter

class RecordBadgesFragment : Fragment() {

    private lateinit var binding: FragmentRecordBadgesBinding
    private val spanCount = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBadgesBinding.inflate(inflater, container, false)

        // 데이터 구성 (원래 LinearLayout에 있던 항목들)
        val rows = buildList {
            add(BadgeRow.Header("확산 배지"))
            add(BadgeRow.Item(R.drawable.img_badge_leaf, "영향력 있는 시작"))
            add(BadgeRow.Item(R.drawable.img_badge_leaf, "입소문 장인"))
            add(BadgeRow.Item(R.drawable.img_badge_leaf, "자석 유저"))
            add(BadgeRow.Item(R.drawable.img_badge_leaf, "친화력 만랩"))

            add(BadgeRow.Header("상호작용 배지"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "대화의 시작"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "친구 신청 왕"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "교류 도자"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "피드백 챔피언"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "댓글 요청"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "응원 마스터"))
            add(BadgeRow.Item(R.drawable.img_badge_trophy, "반응 전문가"))

            add(BadgeRow.Header("기록 배지"))
            add(BadgeRow.Item(R.drawable.img_badge_medal, "도전의 시작"))
            add(BadgeRow.Item(R.drawable.img_badge_medal, "성실한 발자국"))
            add(BadgeRow.Item(R.drawable.img_badge_medal, "루티너"))
            add(BadgeRow.Item(R.drawable.img_badge_medal, "몰입의 날"))

            add(BadgeRow.Header("사용 배지"))
            add(BadgeRow.Item(R.drawable.img_badge_star, "목표 수집가"))
            add(BadgeRow.Item(R.drawable.img_badge_star, "알림 개시"))
            add(BadgeRow.Item(R.drawable.img_badge_star, "분석가"))
            add(BadgeRow.Item(R.drawable.img_badge_star, "꾸준한 기록가"))
        }

        val adapter = BadgeSectionAdapter(rows)

        val glm = GridLayoutManager(requireContext(), spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    if (adapter.getItemViewType(position) == 0) spanCount else 1
            }
        }

        binding.rvBadges.layoutManager = glm
        binding.rvBadges.adapter = adapter

        binding.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        onClickListener()
        return binding.root
    }

    private fun onClickListener(){
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordFragment())
                .commitAllowingStateLoss()
        }
    }
}