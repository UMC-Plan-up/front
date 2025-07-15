package com.example.planup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DailyToDoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.daily_todo_rv)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val dailyToDos = listOf(
            DailyToDo("공부", 75, 5),
            DailyToDo("독서", 100, 5),
            DailyToDo("운동", 50, 3)
        )

        adapter = DailyToDoAdapter(dailyToDos)
        recyclerView.adapter = adapter

        val progressBar = view.findViewById<ProgressBar>(R.id.daily_todo_pb)
        progressBar.progress = 75
    }
}