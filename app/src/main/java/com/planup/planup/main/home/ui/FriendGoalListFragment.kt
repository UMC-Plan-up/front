package com.planup.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.planup.planup.R
import com.planup.planup.component.CircleProfileImageView
import com.planup.planup.main.MainSnackbarViewModel
import com.planup.planup.main.home.adapter.FriendGoalListAdapter
import com.planup.planup.main.home.adapter.FriendGoalWithAchievement
import com.planup.planup.main.home.ui.viewmodel.FriendGoalListViewModel
import com.planup.planup.main.home.ui.viewmodel.FriendGoalUiMessage
import com.planup.planup.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


@AndroidEntryPoint
class FriendGoalListFragment : Fragment() {

//    private var _binding: FragmentFriendGoalListBinding? = null
//    private val binding get() = _binding!!
    private val viewModel: FriendGoalListViewModel by viewModels()
    private val snackbarViewModel: MainSnackbarViewModel by activityViewModels()

    private lateinit var goalAdapter: FriendGoalListAdapter

    companion object {
        const val FRIEND_ID = "friendId"
        const val FRIEND_NAME = "friendName"
        const val FRIEND_PROFILE_IMAGE = "friendResId"

        fun newInstance(
            friendId: Int,
            friendName: String,
            friendResId: String?
        ): FriendGoalListFragment {
            return FriendGoalListFragment().apply {
                arguments = Bundle().apply {
                    putInt(FRIEND_ID, friendId)
                    putString(FRIEND_NAME, friendName)
                    putString(FRIEND_PROFILE_IMAGE, friendResId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.loadTodayAchievement()
        viewModel.loadFriendGoals()
        return content {

            FriendGoalListScreen(
                friendName = viewModel.friendName,
                friendProfileImage = viewModel.friendProfileImage,
                achievementRateFlow = viewModel.achievementRate,
                goalsFlow = viewModel.friendGoals,
                uiMessageFlow = viewModel.friendGoalUiMessage, // 추가
                snackbarViewModel = snackbarViewModel, // 추가
                onBackClick = {
                    parentFragmentManager.popBackStack()
                },
                onGoalClick = { goalId, title ->

                    val fragment = FriendGoalDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt("friendId", viewModel.friendId)
                            putInt("goalId", goalId)
                            putString("title", title)
                        }
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTodayAchievement()
        viewModel.loadFriendGoals()
    }

    @Composable
    fun FriendGoalListScreen(
        friendName: String,
        friendProfileImage: String?,
        achievementRateFlow: StateFlow<Int?>,
        goalsFlow: StateFlow<List<FriendGoalWithAchievement>?>,
        uiMessageFlow: Flow<FriendGoalUiMessage>,
        snackbarViewModel: MainSnackbarViewModel,
        onBackClick: () -> Unit,
        onGoalClick: (Int, String) -> Unit
    ) {
        val achievementRate by achievementRateFlow.collectAsState()
        val goals by goalsFlow.collectAsState()

        LaunchedEffect(Unit) {

            uiMessageFlow.collect { message ->

                when (message) {

                    is FriendGoalUiMessage.Error -> {

                        snackbarViewModel.updateErrorMessage(
                            message.message
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp,bottom = 20.dp)
        ) {

            BackButton(onBackClick)

            Spacer(Modifier.height(16.dp))

            AchievementCard(achievementRate)

            Spacer(Modifier.height(20.dp))

            FriendHeader(friendName, friendProfileImage)

            Spacer(Modifier.height(12.dp))

            FriendGoalRecyclerView(
                goals = goals,
                onGoalClick = {

                    onGoalClick(
                        it.goalId,
                        it.goalName
                    )
                }
            )
        }
    }

    @Composable
    fun BackButton(onBackClick: () -> Unit) {

        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 15.dp)
                .clickable { onBackClick() }
        )
    }

    @Composable
    fun AchievementCard(progress: Int?) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(
                    0xFF5383E3
                )
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            if (progress == null) {
                Text(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    text = "Loading",
                    color = White
                )
            }
            else if (progress == -1){
                Text(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    text = "로딩에 실패했습니다",
                    color = White
                )
            }
            else{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(154.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.fillMaxHeight().padding(start = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "일별 목표 달성률",
                            style = Typography.Medium_XS,
                            color = White
                        )

                        Text(
                            text = "$progress%",
                            style = Typography.Semibold_3XL,
                            color = White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 50.dp) // 외부 여백
                    ) {

                        AndroidView(

                            modifier = Modifier.size(97.dp),

                            factory = { context ->

                                PieChart(context).apply {

                                    val entries = listOf(
                                        PieEntry(progress.toFloat()),
                                        PieEntry((100 - progress).toFloat())
                                    )

                                    val dataSet = PieDataSet(entries, "").apply {

                                        colors = listOf(
                                            android.graphics.Color.WHITE,
                                            android.graphics.Color.rgb(220,220,220)
                                        )

                                        setDrawValues(false)
                                    }

                                    data = PieData(dataSet)

                                    description.isEnabled = false
                                    legend.isEnabled = false
                                    setDrawEntryLabels(false)

                                    setTouchEnabled(false)

                                    isDrawHoleEnabled = true
                                    setHoleColor(android.graphics.Color.TRANSPARENT)

                                    holeRadius = 70f

                                    invalidate()
                                }
                            }
                        )
                    }
                }
            }

        }
    }

    @Composable
    fun FriendHeader(
        friendName: String,
        profileImage: String?
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            CircleProfileImageView(
                modifier = Modifier.size(30.dp),
                profileImage = profileImage
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "$friendName 의 목표 리스트",
                style = Typography.Semibold_L
            )
        }
    }

    @Composable
    fun FriendGoalRecyclerView(
        goals: List<FriendGoalWithAchievement>?,
        onGoalClick: (FriendGoalWithAchievement) -> Unit
    ) {

        val context = LocalContext.current
        var adapter by remember { mutableStateOf<FriendGoalListAdapter?>(null) }
        if (goals == null){
            Text(modifier = Modifier.fillMaxSize(), text = "로딩 중",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = Typography.Semibold_SM)
        }
        else if (goals.isEmpty()){
            Text(modifier = Modifier.fillMaxSize(), text = "목표가 없습니다.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = Typography.Semibold_SM)
        }else{
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {

                    RecyclerView(context).apply {

                        layoutManager = LinearLayoutManager(context)

                        adapter = FriendGoalListAdapter(
                            goals,
                            onGoalClick
                        )
                    }
                },

                update = { recyclerView ->

                    val newAdapter = FriendGoalListAdapter(
                        goals,
                        onGoalClick
                    )

                    recyclerView.adapter = newAdapter

                    adapter = newAdapter
                }
            )
            if (adapter?.hasMoreItems() == true) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Black.copy(alpha = 0.25f),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        )
                        .clickable {

                            adapter?.showMore()
                        },
                    shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),

                ) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = "더보기",
                        textAlign = TextAlign.Center
                    )
                }

            }
        }

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.friendGoalListProfileIv.loadSafeProfile(viewModel.friendProfileImage)
//        binding.friendGoalListTv.text = "${viewModel.friendName}의 목표 리스트"
//
//        setupRecyclerView()
//
//        viewModel.loadTodayAchievement()
//        viewModel.loadFriendGoals()
//
//        val dailyPieChart = binding.friendDailyGoalCompletePc
//        val achievementRate = viewModel.achievementRate.value
//        binding.friendDailyGoalPercentTv.text = "$achievementRate%"
//        setupPieChart(dailyPieChart, achievementRate ?: 0)
//
//
//        observeViewModel()
//
//        binding.friendGoalListBackIv.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }
//    }

//    private fun setupRecyclerView() {
//        goalAdapter = FriendGoalListAdapter(mutableListOf()) { item ->
//            val detailFragment = FriendGoalDetailFragment().apply {
//                arguments = Bundle().apply {
//                    putInt("friendId", viewModel.friendId)
//                    putInt("goalId", item.goalId)
//                    putString("title", item.goalName)
//                }
//            }
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.main_container, detailFragment)
//                .addToBackStack(null)
//                .commit()
//        }
//
//
//        binding.friendGoalListRv.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = goalAdapter
//        }
//
//        binding.friendGoalMoreBtn.setOnClickListener {
//            goalAdapter.showAllItems()
//        }
//    }

//    private fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            launch {
//                repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                    viewModel.friendGoals.collectLatest { goals ->
//                        goalAdapter.updateItems(goals)
//                    }
//                }
//            }
//            launch {
//                repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                    viewModel.achievementRate.collectLatest { rate ->
//                        rate?.let {
//                            binding.friendDailyGoalPercentTv.text = "$it%"
//                            setupPieChart(binding.friendDailyGoalCompletePc, it)
//                        }
//                    }
//                }
//            }
//            launch {
//                repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                    viewModel.friendGoalUiMessage.collect { uiMessage ->
//                        when (uiMessage) {
//                            is FriendGoalUiMessage.Error -> {
//                                snackbarViewModel.updateErrorMessage(uiMessage.message)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//    private fun setupPieChart(pieChart: PieChart, progress: Int) {
//        val entries = listOf(
//            PieEntry(progress.toFloat()),
//            PieEntry((100 - progress).toFloat())
//        )
//        val dataSet = PieDataSet(entries, "").apply {
//            colors = listOf(Color.WHITE, Color.rgb(220, 220, 220))
//            setDrawValues(false)
//            sliceSpace = 2f
//        }
//        pieChart.apply {
//            data = PieData(dataSet)
//            description.isEnabled = false
//            legend.isEnabled = false
//            setDrawEntryLabels(false)
//            setTouchEnabled(false)
//            isDrawHoleEnabled = true
//            setHoleColor(Color.TRANSPARENT)
//            holeRadius = 70f
//            transparentCircleRadius = 0f
//            invalidate()
//        }
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}
