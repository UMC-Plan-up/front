package com.planup.planup.main.home.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentAskNewPenaltyBinding
import com.planup.planup.goal.adapter.RepenaltyAdapter
import com.planup.planup.main.MainActivity
import com.planup.planup.network.controller.ChallengeController
import com.planup.planup.network.dto.challenge.RepenaltyDto

class AskNewPenaltyFragment:Fragment(),RepenaltyAdapter {
    private lateinit var binding: FragmentAskNewPenaltyBinding

    private lateinit var curPenalty: View //현재 선택된 페널티
    private var isFirst: Boolean = true //비활성화할 페널티가 없는 경우

    //DTO에 전달할 데이터
    private lateinit var penalty: String //선택된 페널티
    private var userId: Int = 0 //사용자 id
    private var challengeId: Int = 0 //챌린지 id
    private lateinit var friendIdList: List<Long> //친구 id

    //화면에 출력할 친구 이름
    private var friendName: String = "friend"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskNewPenaltyBinding.inflate(inflater,container,false)
        init()
        textListener()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화 및 데이터 세팅
    private fun init(){
        curPenalty = binding.challengePenaltyCoffeeCl //현재 선택된 페널티 초기화
        if (!isFirst) binding.challengePenaltyOfferCompleteBtn.isActivated = true //페널티가 선택된 경우 버튼 활성화
        //사용자 id
        userId = requireArguments().getInt("userId")
        //챌린지 id
        challengeId = requireArguments().getInt("challengeId")
        //친구 id
        friendIdList = requireArguments().getSerializable("friendIdList") as ArrayList<Long>
        //친구 이름
        friendName = requireArguments().getString("friendName","friend")
        //친구 이름 화면에 출력
        binding.challengePenaltyTitleTv.text = getString(R.string.challenge_request_from,friendName)
    }
    //클릭 이벤트 관리
    private fun clickListener(){
        //이전 버튼 : 종료일, 빈도, 기준기간 설정 페이지로 이동
        binding.challengePenaltyBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.popBackStack()
        }
        //페널티 : 커피
        binding.challengePenaltyCoffeeCl.setOnClickListener {
            selectListener(binding.challengePenaltyCoffeeCl)
            penalty = binding.challengePenaltyCoffeeTv.text.toString()
        }
        //페널티 : 식사 계산
        binding.challengePenaltySnackCl.setOnClickListener {
            selectListener(binding.challengePenaltySnackCl)
            penalty = binding.challengePenaltySnackTv.text.toString()
        }
        //페널티 : 청소
        binding.challengePenaltyCleaningCl.setOnClickListener {
            selectListener(binding.challengePenaltyCleaningCl)
            penalty = binding.challengePenaltyCleaningTv.text.toString()
        }
        //페널티 : 소원
        binding.challengePenaltyWishCl.setOnClickListener {
            selectListener(binding.challengePenaltyWishCl)
            penalty = binding.challengePenaltyWishTv.text.toString()
        }
        //페널티 : 선물
        binding.challengePenaltyPresentCl.setOnClickListener {
            selectListener(binding.challengePenaltyPresentCl)
            penalty = binding.challengePenaltyPresentTv.text.toString()
        }
        //페널티 : 생략
        binding.challengePenaltySkipCl.setOnClickListener {
            selectListener(binding.challengePenaltySkipCl)
        }
        //직접입력 et 클릭 시 선택된 페널티 비활성화
        binding.challengePenaltyEnterEt.setOnFocusChangeListener{ v, hasFocus ->
            if (hasFocus) {
                isFirst = true
                curPenalty.isSelected = false
            }
        }
        //페널티 직접입력 완료 버튼: 조건 만족한 경우만 활성화
        binding.challengePenaltyCompleteBtn.setOnClickListener {
            if (!binding.challengePenaltyCompleteBtn.isActivated) return@setOnClickListener
            penalty = binding.challengePenaltyEnterEt.text.toString()
            binding.challengePenaltyOfferCompleteBtn.isActivated= true //다음 버튼 활성화
            binding.challengePenaltyCompleteBtn.isActivated = false
            binding.challengePenaltyEnterEt.clearFocus() //커서 해제
        }
        //다음 버튼 : 챌린지 신청 완료 화면으로 이동
        binding.challengePenaltyOfferCompleteBtn.setOnClickListener {
            if (!binding.challengePenaltyOfferCompleteBtn.isActivated) return@setOnClickListener
            //서버에 재설정 정보 전달
            val service = ChallengeController()
            service.setRepenaltyAdapter(this)
            service.sendRepenalty(RepenaltyDto(userId, penalty, friendIdList))
        }
    }
    //페널티 직접입력 관리
    private fun textListener() {
        binding.challengePenaltyEnterEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.challengePenaltyEnterEt.text.isNotEmpty()
                    && 30 < binding.challengePenaltyEnterEt.text.toString().length) {
                    //직접 입력한 페널티가 30자 초과하는 경우 입력 불가
                    binding.challengePenaltyErrorTv.visibility = View.VISIBLE //에러메시지 출력
                    binding.challengePenaltyCompleteBtn.isActivated = false //완료 버튼 비활성화
                    binding.challengePenaltyOfferCompleteBtn.isActivated = false //재요청 버튼 비활성화
                } else if (binding.challengePenaltyEnterEt.text.isNotEmpty()
                    && binding.challengePenaltyEnterEt.text.toString().length <= 30){
                    //직접 입력한 페널티가 30자 이내인 경우만 입력 가능
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = true //완료 버튼 활성화
                    binding.challengePenaltyOfferCompleteBtn.isActivated = false //재요청 버튼 비활성화
                } else if(binding.challengePenaltyEnterEt.text.length<1){
                    //페널티 입력되지 않은 경우 버튼 비활성화
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = false //완료 버튼 비활성화
                    binding.challengePenaltyOfferCompleteBtn.isActivated = false //재요청 버튼 비활성화
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }
    //페널티 선택
    private fun selectListener(lastPenalty: View) {
        binding.challengePenaltyEnterEt.clearFocus()
        binding.challengePenaltyEnterEt.text = null //페널티 선택하는 경우 직접 입력한 페널티 초기화됨
        //처음 페널티 선택하는 경우 비활성화 건너뜀
        if (!isFirst) curPenalty.isSelected = false //기존 페널티 비활성화
        isFirst = false //이후에는 페널티 변경할 때마다 기존 페널티 비활성화
        curPenalty = lastPenalty //페널티 업데이트
        curPenalty.isSelected = true //신규 페널티 활성화
        binding.challengePenaltyOfferCompleteBtn.isActivated = true //다음 버튼 활성화
    }

    //API 오류에 대한 메시지
    private fun errorToast(message: String){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }

    //패널티 재설정 API 성공
    override fun successRepenalty() {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commitAllowingStateLoss()
    }
    //페널티 재설정 API 오류
    override fun failRepenalty(message: String) {
        errorToast(message)
    }
}