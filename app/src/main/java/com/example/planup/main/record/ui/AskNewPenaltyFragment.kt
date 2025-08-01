package com.example.planup.main.record.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentAskNewPenaltyBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.ui.HomeFragment

class AskNewPenaltyFragment:Fragment() {
    lateinit var binding: FragmentAskNewPenaltyBinding
    lateinit var selected: View
    lateinit var penalty: String
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
    private fun init() {
        selected = binding.challengePenaltyCoffeeCl //현재 선택된 페널티 초기화
        penalty = "penalty"
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
                    binding.challengePenaltyOfferCompleteBtn.isActivated = false //페널티 제안 완료 버튼 비활성화
                } else if (binding.challengePenaltyEnterEt.text.isNotEmpty()
                    && binding.challengePenaltyEnterEt.text.toString().length <= 30){
                    //직접 입력한 페널티가 30자 이내인 경우만 입력 가능
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = true //완료 버튼 활성화
                    binding.challengePenaltyOfferCompleteBtn.isActivated = true //페널티 제안 완료 버튼 활성화
                } else if(binding.challengePenaltyEnterEt.text.isEmpty()){
                    //페널티 입력되지 않은 경우 버튼 비활성화
                    binding.challengePenaltyErrorTv.visibility = View.GONE //에러메시지 숨김
                    binding.challengePenaltyCompleteBtn.isActivated = false //완료 버튼 비활성화
                    binding.challengePenaltyOfferCompleteBtn.isActivated = false //페널티 제안 완료 버튼 비활성화
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    //클릭 이벤트
    private fun clickListener() {
        //이전 버튼: 챌린지 요청 확인 페이지
        binding.challengePenaltyBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,ReceiveChallengeFragment())
                .commitAllowingStateLoss()
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
            if (hasFocus) selected.isSelected = false
        }
        //페널티 직접입력 완료 버튼: 조건 만족한 경우만 활성화
        binding.challengePenaltyCompleteBtn.setOnClickListener {
            if (!binding.challengePenaltyCompleteBtn.isActivated) return@setOnClickListener
            penalty = binding.challengePenaltyEnterEt.text.toString()
            binding.challengePenaltyOfferCompleteBtn.isActivated= true //페널티 제안 완료 버튼 활성화
            binding.challengePenaltyEnterEt.clearFocus() //커서 해제
        }
        //새로운 페널티 제안 완료 버튼: 홈 프레그먼트로 이동, 상대방 화면에 팝업 설정
        binding.challengePenaltyOfferCompleteBtn.setOnClickListener {
            if (!binding.challengePenaltyOfferCompleteBtn.isActivated) return@setOnClickListener
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
    }

    //페널티 선택
    private fun selectListener(lastPenalty: View) {
        binding.challengePenaltyEnterEt.clearFocus()
        binding.challengePenaltyEnterEt.text = null //페널티 선택하는 경우 직접 입력한 페널티 초기화됨
        //처음 페널티 선택하는 경우 비활성화 건너뜀
        selected.isSelected = false //기존 페널티 비활성화
        selected = lastPenalty //페널티 업데이트
        selected.isSelected = true //신규 페널티 활성화
        binding.challengePenaltyOfferCompleteBtn.isActivated = true //페널티 제안 완료 버튼 활성화
    }
}