package com.example.planup

import FriendChallengeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.FragmentHomeBinding
import com.example.planup.alert.AlertFragment

class HomeFragment : Fragment() {

    private lateinit var dailyRecyclerVIew: RecyclerView
    private lateinit var dailyAdapter: DailyToDoAdapter
    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendChallengeAdapter

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dailyRecyclerVIew = view.findViewById(R.id.daily_todo_rv)
        dailyRecyclerVIew.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val dailyToDos = listOf(
            DailyToDo("공부", 75, 5),
            DailyToDo("독서", 100, 5),
            DailyToDo("운동", 50, 3)
        )

        dailyAdapter = DailyToDoAdapter(dailyToDos)
        dailyRecyclerVIew.adapter = dailyAdapter

        val progressBar = view.findViewById<ProgressBar>(R.id.daily_todo_pb)
        progressBar.progress = 75

        friendRecyclerView = view.findViewById(R.id.home_friend_challenge_rv)
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(30f, 50f, 70f)),
            FriendChallengeItem("블루", "평균 목표 달성률 : 70%", R.drawable.ic_launcher_background, listOf(35f, 45f, 65f))
        )

        friendAdapter = FriendChallengeAdapter(dummyData)
        friendRecyclerView.adapter = friendAdapter
    }

    private fun clickListener(){
        binding.homeAlarmCl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,AlertFragment())
                .commitAllowingStateLoss()
        }
    }
}