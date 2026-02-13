package com.planup.planup.goal.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.planup.planup.R
import com.planup.planup.databinding.FragmentChallengeSetGoalBinding
import com.planup.planup.goal.GoalActivity

/*1:1 챌린지 설정 플로우 챌린지 목표 설정하기
*목표명 및 1회 분량 입력하는 페이지
*/
class ChallengeSetGoalFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetGoalBinding
    //목표명, 1회 분량을 저장하는데 사용
    lateinit var prefs: SharedPreferences
    lateinit var editor:Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetGoalBinding.inflate(inflater, container, false)
        init() //프레그먼트 초기화
        clickListener() //클릭 이벤트 관리
        textListener() //입력 양식 관리
        return binding.root
    }

    private fun init(){
        binding.challengeSetGoalCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.challengeSetGoalCl.height
                binding.challengeSetGoalInnerCl.minHeight = height
                binding.challengeSetGoalCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
    }
    private fun clickListener() {
        //뒤로가기: 목표 설정하기 페이지로 이동, 1:1 챌린지, 커뮤니티 선택 가능
        binding.backIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, GoalSelectFragment())
                .commitAllowingStateLoss()
        }
        //인증방식 설정 페이지로 이동
        binding.btnNextTv.setOnClickListener{
            editor.putString("goalName",binding.goalNameEt.text.toString())
            editor.putString("goalAmount",binding.goalAmountEt.text.toString())
            editor.apply()
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }
    }

    //목표명과 1회분량에 대한 입력 양식을 확인하는 메소드
    private fun textListener() {

        //목표명 입력 시 1글자 이상, 20글자 이내로 작성했는지 확인
        binding.goalNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.goalNameEt.text.toString().isEmpty()) binding.goalNameErrorTv.text =
                    getString(R.string.error_more_one_word)
                else if (20 < binding.goalNameEt.text.toString().length) binding.goalNameErrorTv.text =
                    getString(R.string.error_under_twenty_word)
                else binding.goalNameErrorTv.text = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        //1회 분량 입력 시 1글자 이상, 30글자 이내로 작성했는지 확인
        binding.goalAmountEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.goalAmountEt.text.toString().isEmpty()) binding.goalAmountErrorTv.text =
                    getString(R.string.error_more_one_word)
                else if (30 < binding.goalAmountEt.text.toString().length) binding.goalAmountErrorTv.text =
                    getString(R.string.error_less_thirty_word)
                else binding.goalAmountErrorTv.text = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
}