package com.example.planup.goal.ui
/*1:1 챌린지 설정 플로우 챌린지 목표 설정하기
*목표명 및 1회 분량 입력하는 페이지
*/
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetGoalBinding
import com.example.planup.goal.GoalActivity

class ChallengeSetGoalFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetGoalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetGoalBinding.inflate(inflater, container, false)
        clickListener()
        textListener()
        return binding.root
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
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun textListener() {

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