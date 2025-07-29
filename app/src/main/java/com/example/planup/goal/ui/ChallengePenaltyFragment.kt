package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengePenaltyBinding
import com.example.planup.goal.GoalActivity

class ChallengePenaltyFragment : Fragment() {
    lateinit var binding: FragmentChallengePenaltyBinding
    private lateinit var certification: String //챌린지 인증 방식: 타이머 or 사진
    private lateinit var curPenalty: View //현재 선택된 페널티
    private var isFirst: Boolean = true //비활성화할 페널티가 없는 경우
    private lateinit var penalty: String //최종 페널티
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengePenaltyBinding.inflate(inflater, container, false)
        init()
        textListener()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화 및 데이터 세팅
    private fun init() {
        certification = "timer"//현재 챌린지 인증 방식이 타이머라고 가정
        curPenalty = binding.challengePenaltyCoffeeCl //현재 선택된 페널티 쵝화
        if (!isFirst) binding.challengePenaltyNextBtn.isActivated = true //페널티가 선택된 경우 버튼 활성화
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
                    binding.challengePenaltyNextBtn.isActivated = false //다음 버튼 비활성화
                } else if (binding.challengePenaltyEnterEt.text.isNotEmpty()
                    && binding.challengePenaltyEnterEt.text.toString().length <= 30){
                    //직접 입력한 페널티가 30자 이내인 경우만 입력 가능
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = true //완료 버튼 활성화
                    binding.challengePenaltyNextBtn.isActivated = true //다음 버튼 활성화
                } else if(binding.challengePenaltyEnterEt.text.isEmpty()){
                    //페널티 입력되지 않은 경우 버튼 비활성화
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = false //완료 버튼 비활성화
                    binding.challengePenaltyNextBtn.isActivated = false //다음 버튼 비활성화
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    //클릭 이벤트
    private fun clickListener() {
        //이전 버튼 : 사진 설정 또는 타이머 설정 페이지로 이동
        binding.challengePenaltyBackIv.setOnClickListener {
            when (certification) {
                "photo" ->
                    (context as GoalActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.goal_container, ChallengeSetPhotoFragment())
                        .commitAllowingStateLoss()

                "timer" ->
                    (context as GoalActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.goal_container, ChallengeSetTimerFragment())
                        .commitAllowingStateLoss()
            }
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
//        //직접입력 et 클릭 시 선택된 페널티 비활성화
//        binding.challengePenaltyEnterEt.setOnTouchListener { _, _ ->
//            isFirst = true
//            curPenalty.isSelected = false
//            false // false를 반환해야 EditText의 기본 동작(포커스 + 키보드 열기)이 함께 작동
//        }
        //페널티 직접입력 완료 버튼: 조건 만족한 경우만 활성화
        binding.challengePenaltyCompleteBtn.setOnClickListener {
            if (!binding.challengePenaltyCompleteBtn.isActivated) return@setOnClickListener
            penalty = binding.challengePenaltyEnterEt.text.toString()
            binding.challengePenaltyNextBtn.isActivated= true
        }
        //다음 버튼 : 챌린지 신청 완료 화면으로 이동
        binding.challengePenaltyNextBtn.setOnClickListener {
            if (!binding.challengePenaltyNextBtn.isActivated) return@setOnClickListener
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, Fragment())
                .commitAllowingStateLoss()
        }
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
        binding.challengePenaltyNextBtn.isActivated = true //다음 버튼 활성화
    }
}