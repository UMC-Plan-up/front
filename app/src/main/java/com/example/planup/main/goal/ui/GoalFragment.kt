package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalBinding
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.main.goal.item.GoalAdapter
import com.example.planup.main.goal.item.GoalApiService
import com.example.planup.main.goal.item.GoalRetrofitInstance
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

class GoalFragment : Fragment() {
    private lateinit var prefs : SharedPreferences
    lateinit var binding: FragmentGoalBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var dailyPieChart: PieChart
    private var goals = listOf(
        GoalItem(1, "목표명", "[기준 기간]&[빈도]&\"이상\"", 82),
        GoalItem(2, "토익 공부하기", "매주 5번 이상", 82),
        GoalItem(3, "헬스장 가기", "매일 30분 이상", 82)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        Log.d("GoalFragment","token: $token")
        loadMyGoalList(token)

        dailyPieChart = binding.dailyGoalCompletePc
        setupPieChart(dailyPieChart, 70)

        recyclerView = binding.goalListRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GoalAdapter(
            goals,
            onItemClick = { goalItem ->
                val fragment = GoalDescriptionFragment()
                val descBundle = Bundle().apply {

                }
                fragment.arguments = descBundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { goalId ->
                val intent = Intent(requireContext(), EditFriendGoalActivity::class.java)
                intent.putExtra("goalId", goalId)
                editGoalLauncher.launch(intent) // startActivityForResult 대신 launch 사용
                Log.d("GoalFragment", "goalId: $goalId")
            },
            onDeactivateConfirmed = {
                showDeactivateToast()
            },
            onActivateConfirmed = {
                showActivateToast()
            },
            onDeleteConfirmed = {
                showDeleteToast()
            }
        )
        recyclerView.adapter = adapter
    }

    private fun showDeactivateToast() {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_goal_deactivate, binding.root, false)

        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        toast.show()
    }

    private fun showActivateToast(){
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_goal_activate, binding.root, false)

        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        toast.show()
    }

    private fun showDeleteToast(){
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_goal_delete, binding.root, false)

        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        toast.show()
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
            centerText = ""
            invalidate()
        }
    }

    private fun clickListener() {
        binding.manageButton.setOnClickListener {
            val isEditMode = adapter.toggleEditMode()
            binding.manageButton.text = if (isEditMode) "완료" else "관리"
        }

        binding.unlockTextLl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, SubscriptionPlanFragment())
                .commitAllowingStateLoss()
        }
    }

    private val editGoalLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("GoalFragment", "resultCode: ${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK) {
            val showDialog = result.data?.getBooleanExtra("SHOW_DIALOG", false) ?: false
            if (showDialog) {
                GoalUpdateDialog().show(parentFragmentManager, "GoalUpdateDialog")
            }
        }
    }

    private fun loadMyGoalList(token: String?) {
        if(token == null) {
            Log.d("GoalFragment", "loadMyGoalList token null")
            return
        }
        lifecycleScope.launch {
            try {
                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
                val response = apiService.getMyGoalList(token = "Bearer $token")
                if (response.isSuccess) {
                    val goals = response.result
                    for (goal in goals) {
                        Log.d("GoalFragmentApi","Goal: ${goal.goalName} / type: ${goal.goalType}")
                        goals+GoalItem(goal.goalId, goal.goalName, goal.goalType, 75)
                    }
                } else {
                    Log.d("GoalFragmentApi","loadMyGoalList 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("GoalFragmentApi","네트워크 오류")
            }
        }
    }
}