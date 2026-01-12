package com.example.planup.main.home.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendGoalListBinding
import com.example.planup.extension.loadSafeProfile
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.home.adapter.FriendGoalListAdapter
import com.example.planup.main.home.ui.viewmodel.FriendGoalListViewModel
import com.example.planup.main.home.ui.viewmodel.FriendGoalUiMessage
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendGoalListFragment : Fragment() {

    private var _binding: FragmentFriendGoalListBinding? = null
    private val binding get() = _binding!!
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendGoalListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.friendGoalListProfileIv.loadSafeProfile(viewModel.friendProfileImage)
        binding.friendGoalListTv.text = "${viewModel.friendName}의 목표 리스트"

        setupRecyclerView()

        viewModel.loadTodayAchievement()
        viewModel.loadFriendGoals()

        val dailyPieChart = binding.friendDailyGoalCompletePc
        val achievementRate = viewModel.achievementRate.value
        binding.friendDailyGoalPercentTv.text = "$achievementRate%"
        setupPieChart(dailyPieChart, achievementRate ?: 0)


        observeViewModel()

        binding.friendGoalListBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        goalAdapter = FriendGoalListAdapter(mutableListOf()) { item ->
            val detailFragment = FriendGoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("friendId", viewModel.friendId)
                    putInt("goalId", item.goalId)
                    putString("title", item.goalName)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }


        binding.friendGoalListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
        }

        binding.friendGoalMoreBtn.setOnClickListener {
            goalAdapter.showAllItems()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.friendGoals.collectLatest { goals ->
                        goalAdapter.updateItems(goals)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.achievementRate.collectLatest { rate ->
                        rate?.let {
                            binding.friendDailyGoalPercentTv.text = "$it%"
                            setupPieChart(binding.friendDailyGoalCompletePc, it)
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.friendGoalUiMessage.collect { uiMessage ->
                        when (uiMessage) {
                            is FriendGoalUiMessage.Error -> {
                                snackbarViewModel.updateErrorMessage(uiMessage.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupPieChart(pieChart: PieChart, progress: Int) {
        val entries = listOf(
            PieEntry(progress.toFloat()),
            PieEntry((100 - progress).toFloat())
        )
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.WHITE, Color.rgb(220, 220, 220))
            setDrawValues(false)
            sliceSpace = 2f
        }
        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(false)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 70f
            transparentCircleRadius = 0f
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
