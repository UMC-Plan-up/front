package com.example.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import com.example.planup.goal.adapter.ChallengeFriendsAdapter
import com.example.planup.goal.adapter.FriendRVAdapter
import com.example.planup.goal.adapter.RequestChallengeAdapter
import com.example.planup.network.controller.ChallengeController
import com.example.planup.network.data.challenge.ChallengeFriends
import com.example.planup.network.data.challenge.GetChallengeFriends
import com.example.planup.network.dto.ChallengeDto
import com.example.planup.network.dto.Time

class ChallengeFriendFragment: Fragment(), RequestChallengeAdapter, ChallengeFriendsAdapter {
    lateinit var binding: FragmentChallengeFriendBinding
    lateinit var friends: List<ChallengeFriends> //챌린지 참여를 요청할 수 있는 친구 리스트
    private lateinit var prefs: SharedPreferences //챌린지 정보를 저장함
    private lateinit var editor: Editor //prefs에 데이터 저장
    var friend: ChallengeFriends? = null //최종으로 선택된 친구

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

    //프레그먼트 초기화
    private fun init(){
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
    }

    //클릭 이벤트 관리
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
            requestChallenge()
        }
    }

    //친구 리스트 불러오기
    private fun setFriendList(){
//        val friends = ArrayList<ChallengeFriend>()
//        //임시로 친구 5명 생성
//        friends.add(0,ChallengeFriend(R.drawable.ic_profile_green,"친구1",3))
//        friends.add(1,ChallengeFriend(R.drawable.img_friend_profile_sample2,"친구2",0))
//        friends.add(2,ChallengeFriend(R.drawable.img_friend_profile_sample1,"친구3",1))
//        friends.add(3,ChallengeFriend(R.drawable.profile_example,"친구4",4))
//        friends.add(4,ChallengeFriend(R.drawable.profile_example_2,"친구5",3))
        val service = ChallengeController()
        service.showChallengeFriends(3)
        val adapter = FriendRVAdapter(friends)
        adapter.setMyFriendListener(object : FriendRVAdapter.FriendListener{
            override fun selectFriend(position: Int) {
                Toast.makeText(context,friends[position].nickname,LENGTH_SHORT).show()
                binding.challengeSendCompleteBtn.isActivated = true
                friend = friends[position]
                friend?.id?.let { editor.putInt("friendId", it) }
            }
        })
        binding.challengeSendFriendRv.adapter = adapter
    }
    //챌린지 신청 가능한 친구 리스트 불러오기 성공
    override fun successFriends(friends: List<ChallengeFriends>) {
        this.friends = friends
    }
    //챌린지 신청 가능한 친구 리스트 불러오기 실패
    override fun failFriends(response: String) {
        Toast.makeText(context,response,LENGTH_SHORT).show()
    }

    //챌린지 요청
    private fun requestChallenge(){
        //1:1 챌린지에 대한 DTO 생성
        val challengeDto = ChallengeDto(
            prefs.getString("goalName","no-data")!!,
            prefs.getString("goalAmount","no-data")!!,
            prefs.getString("goalType","no-data")!!,
            prefs.getString("endDay","no-data")!!,
            prefs.getString("status","no-data")!!,
            prefs.getString("penalty","no-data")!!,
            prefs.getInt("friendId",0),
            prefs.getInt("timePerPeriod",0),
            prefs.getInt("frequency",0),
            Time(prefs.getInt("targetTime",0))
        )
        val service = ChallengeController()
        service.requestChallenge(challengeDto)
    }
    //챌린지 요청 성공 시 완료 화면으로 이동
    override fun successRequest() {
        val finishRequestFragment = ChallengeFinishRequestFragment()
        finishRequestFragment.arguments = Bundle().apply {
            putString("friend",friend?.nickname)
        }
        (context as GoalActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.goal_container,finishRequestFragment)
            .commitAllowingStateLoss()
    }
    //챌린지 요청 실패 시 토스트 메시지
    override fun failRequest(response: String) {
        Toast.makeText(context,response, LENGTH_SHORT).show()
    }
}