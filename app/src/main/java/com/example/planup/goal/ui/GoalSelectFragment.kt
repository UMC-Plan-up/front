package com.example.planup.goal.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalSelectBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.data.GoalViewModel
import com.example.planup.main.MainActivity
import com.example.planup.main.goal.ui.GoalFragment

class GoalSelectFragment : Fragment() {
    lateinit var binding: FragmentGoalSelectBinding
    lateinit var category: TextView //선택된 카테고리
    private var isCategory = false //카테고리 선택 처음 클릭했을 때
    private var sleepChallenge: Int = 2 //비활성화된 챌린지 목표 개수
    lateinit var nickname: String //사용자 닉네임

    private val goalViewModel: GoalViewModel by activityViewModels()

    private lateinit var fromWhere: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalSelectBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    //초기화 내용
    private fun init() {
        fromWhere = goalViewModel.fromWhere.value?.toString() ?: "no-value"
//        goalViewModel.fromWhere.observe(viewLifecycleOwner) { value ->
//            fromWhere = value ?: "null"
//            // 여기에서 정확한 값을 사용할 수 있음
//        }
        category = binding.categoryStudyTv
        nickname = arguments?.getString("GoalOwnerName", "사용자").toString()
        binding.goalSelectCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.goalSelectCl.height  // 화면에 보이는 영역 높이 (픽셀 단위)
                binding.goalSelectInnerCl.minHeight = height
                binding.goalSelectCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    //클릭 이벤트 처리
    private fun clickListener() {
        val select = ContextCompat.getColor(context, R.color.blue_200)
        val unselect = ContextCompat.getColor(context, R.color.black_400)
        val unselectCategory = ContextCompat.getColor(context, R.color.blue_200)

        //뒤로가기
        binding.goalSelectBackIv.setOnClickListener {
            when(fromWhere){
                "CommunityIntroFragment" -> {
                    val inflater = LayoutInflater.from(context)
                    val layout = inflater.inflate(R.layout.toast_grey_template,null)
                    layout.findViewById<TextView>(R.id.toast_grey_template_tv).setText(R.string.toast_make_new_goal)

                    val toast = Toast(context)
                    toast.view = layout
                    toast.duration = LENGTH_SHORT
                    toast.setGravity(Gravity.BOTTOM,0,400)
                    toast.show()
                }
                "GoalFragment" -> {
                    val intent = Intent(context as GoalActivity, MainActivity::class.java)
                    intent.apply {
                        putExtra("FROM_CHALLENGE_TO", "GoalFragment")
                    }
                    startActivity(intent)
                }
                "ChallengeCompleteFragment" -> {
                    val intent = Intent(context as GoalActivity, MainActivity::class.java)
                    intent.apply {
                        putExtra("FROM_CHALLENGE_TO","RecordFragment")
                    }
                    startActivity(intent)
                }
            }
        }
        //함께 목표 설정하기 클릭
        binding.goalSelectTogetherTv.setOnClickListener {
            if (!binding.goalSelectTogetherTv.isSelected) {
                //함께 설정하기 선택
                binding.goalSelectTogetherTv.isSelected = true
                binding.goalSelectTogetherTv.setTextColor(select)
                //카테고리 활성화
                binding.goalCategoryCl.visibility = View.VISIBLE
                isCategory = false
                //챌린지 해제
                binding.goalSelectChallengeCl.isSelected = false
                binding.goalSelectChallengeTv.setTextColor(unselect)
                binding.goalSelectPenaltyTv.setTextColor(unselect)
            }
        }

        //1:1 챌린지 도전하기 클릭
        binding.goalSelectChallengeCl.setOnClickListener {
            //함께 설정하기 해제
            binding.goalSelectTogetherTv.isSelected = false
            //선택된 카테고리 비활성화
            category.isSelected = false
            category.setTextColor(unselectCategory)
            binding.categoryErrorTv.text = null
            //직접설정인 경우 입력란 초기화
            binding.customCategoryEt.visibility = View.GONE
            binding.customCategoryEt.text = null
            //챌린지 선택
            binding.goalSelectChallengeCl.isSelected = true
            binding.goalSelectTogetherTv.setTextColor(unselect)
            binding.goalSelectChallengeTv.setTextColor(select)
            binding.goalSelectPenaltyTv.setTextColor(select)
            binding.goalCategoryCl.visibility = View.GONE
        }

        //카테고리 선택 이벤트 처리
        binding.categoryStudyTv.setOnClickListener {
            showCategory(binding.categoryStudyTv)
        }
        binding.categoryBookTv.setOnClickListener {
            showCategory(binding.categoryBookTv)
        }
        binding.categoryMeditationTv.setOnClickListener {
            showCategory(binding.categoryMeditationTv)
        }
        binding.categorySleepTv.setOnClickListener {
            showCategory(binding.categorySleepTv)
        }
        binding.categoryEatTv.setOnClickListener {
            showCategory(binding.categoryEatTv)
        }
        binding.categoryInstrumentTv.setOnClickListener {
            showCategory(binding.categoryInstrumentTv)
        }
        binding.categoryExerciseTv.setOnClickListener {
            showCategory(binding.categoryExerciseTv)
        }
        binding.categoryDiaryText.setOnClickListener {
            showCategory(binding.categoryDiaryText)
        }
        //직접 선택 카테고리
        binding.categoryCustomTv.setOnClickListener {
            binding.customCategoryEt.visibility = View.VISIBLE
            showCategory(binding.categoryCustomTv)
        }

        //다음 버튼 클릭 후 선택한 목표에 따른 페이지 이동
        binding.goalSelectNextBtn.setOnClickListener {
            if (binding.goalSelectTogetherTv.isSelected) {
                if (!isCategory) {
                    binding.categoryErrorTv.setText(R.string.error_category)
                    return@setOnClickListener
                }
                val commonGoalFragment = CommonGoalFragment()
                commonGoalFragment.arguments = Bundle().apply{
                    putString("goalOwnerName", nickname)
                    putString("selectedCategory", category.text.toString())
                    putString("goalType","COMMUNITY")
                }
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container, CommonGoalFragment())
                    .commitAllowingStateLoss()
            } else if (binding.goalSelectChallengeCl.isSelected) {
                if (sleepChallenge == 2) {
                    makeToast()
                    sleepChallenge--
                    return@setOnClickListener
                }
                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container, ChallengeSetGoalFragment())
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun makeToast() {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template_s, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text =
            getString(R.string.toast_challenge_setting)

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 330)
        toast.show()
    }

    //선택된 카테고리 보여주는 함수
    private fun showCategory(selected: TextView) {
        val select = ContextCompat.getColor(context, R.color.white_font) //선택된 카테고리
        val unselect = ContextCompat.getColor(context, R.color.blue_200) //선택되지 않은 카테고리

        if (!isCategory) { //카테고리를 아직 선택하지 않은 경우 이 조건문은 실행되지 않음
            category = selected
            isCategory = true
        } else {
            category.isSelected = false
            category.setTextColor(unselect)
            //커스텀 카테고리인 경우 목표 입력란 삭제
            if (category == binding.categoryCustomTv && selected != binding.categoryCustomTv) {
                binding.customCategoryEt.visibility = View.GONE
                binding.customCategoryEt.text = null //입력한 목표 제거
                category.clearFocus() //커서 비활성화
                binding.categoryErrorTv.text = null //에러메시지 제거
            }
        }
        //현재 선택된 카테고리 업데이트
        category = selected
        category.isSelected = true
        category.setTextColor(select)
        //선택된 카테고리가 직접 설정인 경우
        textListener()
    }

    private fun textListener() {
        binding.customCategoryEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.customCategoryEt.isFocused
                    && binding.customCategoryEt.text.toString().isEmpty()
                ) {
                    binding.categoryErrorTv.setText(R.string.error_category_name)
                } else {
                    binding.categoryErrorTv.text = null
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }
}