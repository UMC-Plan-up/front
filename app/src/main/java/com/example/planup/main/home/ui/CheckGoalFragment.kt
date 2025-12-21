package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentCheckGoalBinding
import com.example.planup.main.home.data.DailyToDo
import com.example.planup.main.home.adapter.DailyToDoAdapter

class CheckGoalFragment : Fragment() {
    private lateinit var binding: FragmentCheckGoalBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyToDoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.dailyTodoRv
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val dailyToDos = listOf(
            DailyToDo("공부", 75, 5),
            DailyToDo("독서", 100, 5),
            DailyToDo("운동", 50, 3)
        )

        adapter = DailyToDoAdapter()
        recyclerView.adapter = adapter

        val progressBar = binding.dailyTodoPb
        progressBar.progress = 75
    }
}