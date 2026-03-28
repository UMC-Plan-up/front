package com.planup.planup.main.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.planup.planup.databinding.FragmentHomeAlertBinding
import com.planup.planup.main.home.adapter.NotificationAdapter
import com.planup.planup.main.home.ui.viewmodel.HomeAlertViewModel
import com.planup.planup.main.home.ui.viewmodel.NotificationCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeAlertFragment : Fragment() {
    lateinit var binding: FragmentHomeAlertBinding
    private val viewModel: HomeAlertViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter
    val goal = NotificationCategory.GOAL
    val feedback = NotificationCategory.FEEDBACK
    val challenge = NotificationCategory.CHALLENGE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAlertBinding.inflate(inflater, container, false)
        viewModel.userId = arguments?.getInt("userId") ?: 0
        Log.d("HomeAlertFragment", "userId: ${viewModel.userId}")
        clickListener()
        adapter = NotificationAdapter{
            viewModel.onClicked(it)
        }

        binding.homeAlertRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeAlertFragment.adapter

        }

        // 🔥 리스트 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notificationList.collect { list ->
                adapter.submitList(list)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategory.collect { category ->
                updateFilterUI(category)
            }

        }

        // 버튼 클릭
        binding.btnGoal.setOnClickListener {
            viewModel.selectCategory(goal)
        }

        binding.btnReaction.setOnClickListener {
            viewModel.selectCategory(feedback)
        }

        binding.btnChallenge.setOnClickListener {
            viewModel.selectCategory(challenge)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategory.collect { category ->
                Log.d("HomeAlertFragment", "selectedCategory: $category")
                viewModel.loadNotifications()
            }
        }

        return binding.root
    }

    private fun clickListener(){
        binding.homeAlertBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun updateFilterUI(selected: NotificationCategory) {
        binding.btnGoal.isSelected = selected == goal
        binding.btnReaction.isSelected = selected == feedback
        binding.btnChallenge.isSelected = selected == challenge
    }

}