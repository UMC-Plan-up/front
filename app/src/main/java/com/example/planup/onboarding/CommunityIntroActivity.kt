package com.example.planup.onboarding

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.SemanticB5
import com.example.planup.theme.Typography
import kotlinx.coroutines.launch

class CommunityIntroActivity : AppCompatActivity() {

    private val goalData = mapOf(
        "공부하기" to listOf(
            GoalItemModel("매일 2시간 이상 영어 공부하기", R.drawable.ic_book),
            GoalItemModel("매주 3회 이상 온라인 강의 듣기", R.drawable.ic_laptop),
            GoalItemModel("매일 30분 이상 문제집 풀기", R.drawable.ic_notebook),
            GoalItemModel("매주 2회 이상 스터디 모임 참석하기", R.drawable.ic_people)
        ),
        "독서하기" to listOf(
            GoalItemModel("매일 30분 이상 책 읽기", R.drawable.ic_book2),
            GoalItemModel("주 3회 이상 독서 노트 작성하기", R.drawable.ic_notebook2),
            GoalItemModel("매달 1회 이상 책 완독하기", R.drawable.ic_check_green),
            GoalItemModel("매달 2회 이상 독서 모임 참석하기", R.drawable.ic_coffee2)
        ),
        "명상하기" to listOf(
            GoalItemModel("매일 10분 이상 명상하기", R.drawable.ic_meditation),
            GoalItemModel("매주 5회 이상 아침 명상하기", R.drawable.ic_sunrise),
            GoalItemModel("매일 1회 이상 심호흡 5분 하기", R.drawable.ic_breath),
            GoalItemModel("매주 1회 이상 명상, 스트레칭 하기", R.drawable.ic_yoga)
        ),
        "수면 패턴 관리" to listOf(
            GoalItemModel("매주 3회 이상 자정 이전 취침", R.drawable.ic_bed),
            GoalItemModel("매일 아침 7시 이전 일어나기", R.drawable.ic_clock),
            GoalItemModel("매일 8시간 이상 수면 취하기", R.drawable.ic_sleep),
            GoalItemModel("주 5회 이상 자기 전 스마트폰 금지", R.drawable.ic_phone_off)
        ),
        "식습관 관리" to listOf(
            GoalItemModel("매일 아침 식사 챙겨먹기", R.drawable.ic_salad),
            GoalItemModel("주 3회 이상 야채 위주 식사", R.drawable.ic_vitamin),
            GoalItemModel("하루 물 2L 이상 마시기", R.drawable.ic_water),
            GoalItemModel("주 1회 이상 건강식 요리하기", R.drawable.ic_rice)
        ),
        "악기 배우기" to listOf(
            GoalItemModel("매일 30분 이상 피아노 연습하기", R.drawable.ic_piano),
            GoalItemModel("매주 3회 이상 기타 코드 연습하기", R.drawable.ic_guitar),
            GoalItemModel("매주 1곡 이상 악보 완주하기", R.drawable.ic_sheet),
            GoalItemModel("매주 1회 이상 드럼 레슨 받기", R.drawable.ic_drum)
        ),
        "운동하기" to listOf(
            GoalItemModel("주 3회 이상 필라테스 수업 듣기", R.drawable.ic_tennis),
            GoalItemModel("주 1회 이상 10층 계단 오르기", R.drawable.ic_boxing),
            GoalItemModel("매일 30분 러닝하기", R.drawable.ic_hockey),
            GoalItemModel("하루 한 번 이상 헬스장 가기", R.drawable.ic_flag)
        ),
        "일기 쓰기" to listOf(
            GoalItemModel("매주 3회 이상 모닝 페이지 작성하기", R.drawable.ic_pencil),
            GoalItemModel("매일 감사 일기 쓰기", R.drawable.ic_note),
            GoalItemModel("매주 1회 이상 주간 일기 쓰기", R.drawable.ic_calendar),
            GoalItemModel("매일 감정 일기 쓰기", R.drawable.ic_emotion)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nickname = intent.getStringExtra("nickname") ?: "사용자"

        val tabs = mutableListOf<String>()
        val items = mutableListOf<List<GoalItemModel>>()

        goalData.forEach { tabItem, goalItems ->
            tabs.add(tabItem)
            items.add(goalItems)
        }

        setContent {
            val pagerState = rememberPagerState(0) { tabs.size }

            CommunityIntroScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                pagerState = pagerState,
                nickname = nickname,
                tabs = tabs,
                items = items
            )
        }
    }
}

data class GoalItemModel(
    val goal: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun CommunityIntroScreen(
    pagerState: PagerState,
    nickname: String,
    tabs: List<String>,
    items: List<List<GoalItemModel>>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val tabIndex = pagerState.currentPage

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(top = 64.dp, start = 14.dp, end = 14.dp),
            text = stringResource(R.string.community_greeting, nickname),
            style = Typography.Medium_2XL
        )

        SecondaryScrollableTabRow(
            modifier = Modifier
                .padding(top = 8.dp),
            selectedTabIndex = tabIndex,
            containerColor = Color.White,
            contentColor = Color.White,
            indicator =
                @Composable {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabIndex),
                        color = SemanticB4
                    )
                },
            divider = @Composable { HorizontalDivider() },
            edgePadding = 0.dp,
            tabs = {
                tabs.forEachIndexed { index, community ->
                    Tab(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(60.dp),
                        selected = tabIndex == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = community,
                                style = Typography.Medium_SM
                            )
                        }
                    )
                }
            }
        )
        HorizontalPager(
            modifier = Modifier.weight(1.0f),
            state = pagerState
        ) { page ->
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                items[page].forEach { goalItem ->
                    Row(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(goalItem.iconRes),
                            tint = Color.Unspecified,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(22.dp))
                        Text(
                            text = goalItem.goal,
                            style = Typography.Medium_SM
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = SemanticB5,
                        thickness = 1.dp
                    )
                }

            }
        }


        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_community_category_goal),
            onClick = { }
        )

        Spacer(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(12.dp)
        )
    }
}

@Preview
@Composable
private fun CommunityIntroScreenPreview() {
    val goalData = mapOf(
        "공부하기" to listOf(
            GoalItemModel("매일 2시간 이상 영어 공부하기", R.drawable.ic_book),
            GoalItemModel("매주 3회 이상 온라인 강의 듣기", R.drawable.ic_laptop),
            GoalItemModel("매일 30분 이상 문제집 풀기", R.drawable.ic_notebook),
            GoalItemModel("매주 2회 이상 스터디 모임 참석하기", R.drawable.ic_people)
        ),
        "독서하기" to listOf(
            GoalItemModel("매일 30분 이상 책 읽기", R.drawable.ic_book2),
            GoalItemModel("주 3회 이상 독서 노트 작성하기", R.drawable.ic_notebook2),
            GoalItemModel("매달 1회 이상 책 완독하기", R.drawable.ic_check_green),
            GoalItemModel("매달 2회 이상 독서 모임 참석하기", R.drawable.ic_coffee2)
        ),
        "명상하기" to listOf(
            GoalItemModel("매일 10분 이상 명상하기", R.drawable.ic_meditation),
            GoalItemModel("매주 5회 이상 아침 명상하기", R.drawable.ic_sunrise),
            GoalItemModel("매일 1회 이상 심호흡 5분 하기", R.drawable.ic_breath),
            GoalItemModel("매주 1회 이상 명상, 스트레칭 하기", R.drawable.ic_yoga)
        ),
        "수면 패턴 관리" to listOf(
            GoalItemModel("매주 3회 이상 자정 이전 취침", R.drawable.ic_bed),
            GoalItemModel("매일 아침 7시 이전 일어나기", R.drawable.ic_clock),
            GoalItemModel("매일 8시간 이상 수면 취하기", R.drawable.ic_sleep),
            GoalItemModel("주 5회 이상 자기 전 스마트폰 금지", R.drawable.ic_phone_off)
        ),
        "식습관 관리" to listOf(
            GoalItemModel("매일 아침 식사 챙겨먹기", R.drawable.ic_salad),
            GoalItemModel("주 3회 이상 야채 위주 식사", R.drawable.ic_vitamin),
            GoalItemModel("하루 물 2L 이상 마시기", R.drawable.ic_water),
            GoalItemModel("주 1회 이상 건강식 요리하기", R.drawable.ic_rice)
        ),
        "악기 배우기" to listOf(
            GoalItemModel("매일 30분 이상 피아노 연습하기", R.drawable.ic_piano),
            GoalItemModel("매주 3회 이상 기타 코드 연습하기", R.drawable.ic_guitar),
            GoalItemModel("매주 1곡 이상 악보 완주하기", R.drawable.ic_sheet),
            GoalItemModel("매주 1회 이상 드럼 레슨 받기", R.drawable.ic_drum)
        ),
        "운동하기" to listOf(
            GoalItemModel("주 3회 이상 필라테스 수업 듣기", R.drawable.ic_tennis),
            GoalItemModel("주 1회 이상 10층 계단 오르기", R.drawable.ic_boxing),
            GoalItemModel("매일 30분 러닝하기", R.drawable.ic_hockey),
            GoalItemModel("하루 한 번 이상 헬스장 가기", R.drawable.ic_flag)
        ),
        "일기 쓰기" to listOf(
            GoalItemModel("매주 3회 이상 모닝 페이지 작성하기", R.drawable.ic_pencil),
            GoalItemModel("매일 감사 일기 쓰기", R.drawable.ic_note),
            GoalItemModel("매주 1회 이상 주간 일기 쓰기", R.drawable.ic_calendar),
            GoalItemModel("매일 감정 일기 쓰기", R.drawable.ic_emotion)
        )
    )

    CommunityIntroScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        pagerState = rememberPagerState(0) { goalData.size },
        nickname = "닉네임",
        tabs = goalData.keys.toList(),
        items = goalData.values.toList()
    )
}