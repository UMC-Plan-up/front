package com.example.planup.main.home.ui

import android.app.Dialog
import com.example.planup.main.home.adapter.FriendChallengeAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.home.item.FriendChallengeItem
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentHomeBinding
import com.example.planup.main.home.data.DailyToDo
import com.example.planup.main.home.adapter.DailyToDoAdapter
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.main.record.ui.ReceiveChallengeFragment

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
            FriendChallengeItem(
                "블루",
                "평균 목표 달성률 : 70%",
                R.drawable.ic_launcher_background,
                listOf(30f, 50f, 70f)
            ),
            FriendChallengeItem(
                "블루",
                "평균 목표 달성률 : 70%",
                R.drawable.ic_launcher_background,
                listOf(35f, 45f, 65f)
            )
        )

        friendAdapter = FriendChallengeAdapter(dummyData)
        friendRecyclerView.adapter = friendAdapter
    }

    private fun clickListener() {
        binding.homeAlarmCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeAlertFragment())
                .commitAllowingStateLoss()
        }
        binding.imageView5.setOnClickListener {
            showPopup()
        }
    }
    private fun showPopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_challenge)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.findViewById<TextView>(R.id.popup_challenge_notice_tv).text = getString(R.string.popup_challenge_request,"그린")
        dialog.findViewById<TextView>(R.id.popup_challenge_btn).setOnClickListener{
            dialog.dismiss()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,ReceiveChallengeFragment())
                .commitAllowingStateLoss()
        }
        dialog.show()
    }
}