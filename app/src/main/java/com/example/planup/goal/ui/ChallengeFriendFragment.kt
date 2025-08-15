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
import com.example.planup.network.data.ChallengeFriends
import com.example.planup.network.dto.GoalPeriod
import com.example.planup.network.dto.challenge.ChallengeDto
import com.example.planup.network.dto.challenge.Time

class ChallengeFriendFragment: Fragment(), RequestChallengeAdapter, ChallengeFriendsAdapter {
    lateinit var binding: FragmentChallengeFriendBinding
    lateinit var friends: List<ChallengeFriends> //챌린지 참여를 요청할 수 있는 친구 리스트
    private lateinit var prefs: SharedPreferences //챌린지 정보를 저장함
    private lateinit var editor: Editor //prefs에 데이터 저장
    var friend: ChallengeFriends? = null //최종으로 선택된 친구
    private lateinit var challengeService: ChallengeController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeFriendBinding.inflate(inflater,container,false)
        init()
        clickListener()
        //setFriendList()
        return binding.root
    }

    //프레그먼트 초기화
    private fun init(){
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
        val userPrefs = (context as GoalActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        challengeService = ChallengeController()
        challengeService.showChallengeFriends(userPrefs.getInt("userId",0))
        challengeService.setChallengeFriendsAdapter(this)
        challengeService.setRequestChallengeAdapter(this)
        friend = ChallengeFriends(0,"no-data",0)
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
            //if (!binding.challengeSendCompleteBtn.isActivated) return@setOnClickListener
            requestChallenge(friend!!.id)
        }
    }

    //친구 리스트 불러오기
    private fun setFriendList(){
        val adapter = FriendRVAdapter(friends)
        adapter.setMyFriendListener(object : FriendRVAdapter.FriendListener{
            override fun selectFriend(position: Int) {
                Toast.makeText(context,friends[position].nickname,LENGTH_SHORT).show()
                binding.challengeSendCompleteBtn.isActivated = true
                friend = friends[position]
            }
        })
        binding.challengeSendFriendRv.adapter = adapter
    }
    //챌린지 신청 가능한 친구 리스트 불러오기 성공
    override fun successFriends(friends: List<ChallengeFriends>) {
        this.friends = friends
        setFriendList()
    }
    //챌린지 신청 가능한 친구 리스트 불러오기 실패
    override fun failFriends(response: String) {
        Toast.makeText(context,response,LENGTH_SHORT).show()
    }

    //챌린지 요청
    private fun requestChallenge(friendId: Int){
        //1:1 챌린지에 대한 DTO 생성
//        val challengeDto = ChallengeDto(
//            prefs.getString("goalName","no-data")!!, //목표명
//            prefs.getString("goalAmount","no-data")!!, //1회 분량
//            prefs.getString("goalType","no-data")!!, //인증 방식
//            prefs.getInt("targetTime",0), //oneDose: 1회 분량 중 분량 값(추후 제거 예정)
//            prefs.getString("endDate","no-data")!!, //종료일
//            prefs.getString("status","no-data")!!, //요청 형태
//            prefs.getString("penalty","no-data")!!, //페널티
//            friendId, //친구 id
//            prefs.getString("timePerPeriod","no-data")!!, //기준기간
//            prefs.getInt("frequency",0), //빈도
//            Time(prefs.getInt("targetTime",0)) //타이머 총 시간
//        )
        //임시 데이터 / 기준기간을 "DAY"로 설정
        val challengeDto = ChallengeDto(
            prefs.getString("goalName","no-data")!!, //목표명
            prefs.getString("goalAmount","no-data")!!, //1회 분량
            prefs.getString("goalType","no-data")!!, //인증 방식
            prefs.getInt("targetTime",0), //oneDose: 1회 분량 중 분량 값(추후 제거 예정)
            prefs.getString("endDate","no-data")!!, //종료일
            prefs.getString("status","no-data")!!, //요청 형태
            prefs.getString("penalty","no-data")!!, //페널티
            friendId, //친구 id
            GoalPeriod.DAY, //기준기간
            prefs.getInt("frequency",0), //빈도
            Time(prefs.getInt("targetTime",0)) //타이머 총 시간
        )
        challengeService.requestChallenge(challengeDto)
    }
    //챌린지 요청 성공 시 완료 화면으로 이동
    override fun successRequest() {
        //sharedPreferences 데이터 해제
        editor.clear()
        editor.apply()
        //완료화면에 띄울 친구 이름 저장
        val finishRequestFragment = ChallengeFinishRequestFragment()
        finishRequestFragment.arguments = Bundle().apply {
            putString("friend",friend?.nickname)
        }
        //완료 화면으로 이동
        (context as GoalActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.goal_container,finishRequestFragment)
            .commitAllowingStateLoss()
    }
    //챌린지 요청 실패 시 토스트 메시지
    override fun failRequest(response: String) {
        Toast.makeText(context,response, LENGTH_SHORT).show()
    }
}