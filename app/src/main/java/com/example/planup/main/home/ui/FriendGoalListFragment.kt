package com.example.planup.main.home.ui

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendGoalListBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.FriendGoalListAdapter
import com.example.planup.main.home.ui.viewmodel.FriendGoalListViewModel
import com.example.planup.network.ApiResult
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendGoalListFragment : Fragment() {

    private var _binding: FragmentFriendGoalListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendGoalListViewModel by viewModels()

    private lateinit var goalAdapter: FriendGoalListAdapter
    private var friendId: Int = 0
    private lateinit var friendName: String
    private lateinit var friendProfileImage: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendGoalListBinding.inflate(inflater, container, false)
        friendId = arguments?.getInt("friendId") ?: 0
        friendName = arguments?.getString("friendName") ?: ""
        friendProfileImage = arguments?.getString("friendResId") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this).load(friendProfileImage).circleCrop().into(binding.friendGoalListProfileIv)
        binding.friendGoalListTv.text = "${friendName}의 목표 리스트"

        setupRecyclerView()

        viewModel.loadTodayAchievement(createErrorHandler("loadTodayAchievement"))
        viewModel.loadFriendGoals(friendId, createErrorHandler("loadFriendGoals"))

        observeViewModel()

        binding.friendGoalListBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        goalAdapter = FriendGoalListAdapter(mutableListOf()) { item ->
            val detailFragment = FriendGoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("friendId", friendId)
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
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.friendGoals.collectLatest { goals ->
                goalAdapter.updateItems(goals)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.achievementRate.collectLatest { rate ->
                rate?.let {
                    binding.friendDailyGoalPercentTv.text = "$it%"
                    setupPieChart(binding.friendDailyGoalCompletePc, it)
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

    fun <T> createErrorHandler(tag: String): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
                else -> {}
            }
        }
    }
}
