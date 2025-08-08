package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeFriendBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.FriendRVAdapter
import com.example.planup.goal.data.ChallengeFriend

class ChallengeFriendFragment: Fragment() {
    lateinit var binding: FragmentChallengeFriendBinding
    lateinit var friend:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeFriendBinding.inflate(inflater,container,false)
        init()
        clickListener()
        setFriendList()
        return binding.root
    }

    private fun init(){
        friend = "friend"
    }
    private fun clickListener(){
        //뒤로가기: 페널티 선택 페이지로 이동
        binding.challengeSendBackIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengePenaltyFragment())
                .commitAllowingStateLoss()
        }
        //완료 버튼: 챌린지 참여 완료 페이지로 이동
        binding.challengeSendCompleteBtn.setOnClickListener {
            if (!binding.challengeSendCompleteBtn.isActivated) return@setOnClickListener
            //최종으로 선택된 친구 이름 전달
            //sharedPreferences로 관리하거나 DTO에 넣어서 관리하는거로 수정해도 될듯
            val finishRequestFragment = ChallengeFinishRequestFragment()
            finishRequestFragment.arguments = Bundle().apply {
                putString("friend",friend)
            }
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,finishRequestFragment)
                .commitAllowingStateLoss()
        }
    }
    private fun setFriendList(){
        val friends = ArrayList<ChallengeFriend>()
        //임시로 친구 5명 생성
        friends.add(0,ChallengeFriend(R.drawable.ic_profile_green,"친구1",3))
        friends.add(1,ChallengeFriend(R.drawable.img_friend_profile_sample2,"친구2",0))
        friends.add(2,ChallengeFriend(R.drawable.img_friend_profile_sample1,"친구3",1))
        friends.add(3,ChallengeFriend(R.drawable.profile_example,"친구4",4))
        friends.add(4,ChallengeFriend(R.drawable.profile_example_2,"친구5",3))

        val adapter = FriendRVAdapter(friends)
        adapter.setMyFriendListener(object : FriendRVAdapter.FriendListener{
            override fun selectFriend(position: Int) {
                Toast.makeText(context,friends[position].name,LENGTH_SHORT).show()
                binding.challengeSendCompleteBtn.isActivated = true
                friend = friends[position].name
            }
        })
        binding.challengeSendFriendRv.adapter = adapter
    }
}