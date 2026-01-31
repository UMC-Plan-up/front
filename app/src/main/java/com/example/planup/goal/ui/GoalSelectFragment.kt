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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalSelectBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.goal.viewmodel.GoalViewModel
import com.example.planup.main.MainActivity

class GoalSelectFragment : Fragment() {
    lateinit var binding: FragmentGoalSelectBinding
    lateinit var category: TextView // 선택된 카테고리
    private var isCategory = false // 카테고리 선택 여부
    private var sleepChallenge: Int = 2 // 비활성화된 챌린지 목표 개수
    lateinit var nickname: String // 사용자 닉네임

    private val goalViewModel: GoalViewModel by activityViewModels()
    private lateinit var fromWhere: String

    // 카테고리 매핑
    private val categoryMap: MutableMap<TextView, String> by lazy {
        mutableMapOf(
            binding.categoryStudyTv to "STUDYING",
            binding.categoryBookTv to "READING",
            binding.categoryMeditationTv to "MEDITATION",
            binding.categorySleepTv to "SLEEP_PATTERN",
            binding.categoryEatTv to "EATING",
            binding.categoryInstrumentTv to "MUSIC",
            binding.categoryExerciseTv to "EXERCISE",
            binding.categoryDiaryText to "DIARY",
            binding.categoryCustomTv to "SELF" // 커스텀 카테고리
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalSelectBinding.inflate(inflater, container, false)

        init()
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }
    }

    private fun init() {
        fromWhere = goalViewModel.fromWhere.value?.toString() ?: "no-value"
        category = binding.categoryStudyTv
        nickname = arguments?.getString("goalOwnerName", "사용자").toString()

        binding.goalSelectCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.goalSelectCl.height
                binding.goalSelectInnerCl.minHeight = height
                binding.goalSelectCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun clickListener() {
        val select = ContextCompat.getColor(context, R.color.blue_200)
        val unselect = ContextCompat.getColor(context, R.color.black_400)
        val unselectCategory = ContextCompat.getColor(context, R.color.blue_200)

        // 뒤로가기
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
                    intent.putExtra("FROM_CHALLENGE_TO", "GoalFragment")
                    startActivity(intent)
                }
                "ChallengeCompleteFragment" -> {
                    val intent = Intent(context as GoalActivity, MainActivity::class.java)
                    intent.putExtra("FROM_CHALLENGE_TO","RecordFragment")
                    startActivity(intent)
                }
            }
        }

        // 함께 목표 설정하기 클릭
        binding.goalSelectTogetherTv.setOnClickListener {
            if (!binding.goalSelectTogetherTv.isSelected) {
                binding.goalSelectTogetherTv.isSelected = true
                binding.goalSelectTogetherTv.setTextColor(select)
                binding.goalCategoryCl.visibility = View.VISIBLE
                isCategory = false
                binding.goalSelectChallengeCl.isSelected = false
                binding.goalSelectChallengeTv.setTextColor(unselect)
                binding.goalSelectPenaltyTv.setTextColor(unselect)
            }
        }

        // 1:1 챌린지 도전하기 클릭
        binding.goalSelectChallengeCl.setOnClickListener {
            binding.goalSelectTogetherTv.isSelected = false
            category.isSelected = false
            category.setTextColor(unselectCategory)
            binding.categoryErrorTv.text = null
            binding.customCategoryEt.visibility = View.GONE
            binding.customCategoryEt.text = null
            binding.goalSelectChallengeCl.isSelected = true
            binding.goalSelectTogetherTv.setTextColor(unselect)
            binding.goalSelectChallengeTv.setTextColor(select)
            binding.goalSelectPenaltyTv.setTextColor(select)
            binding.goalCategoryCl.visibility = View.GONE
        }

        // 카테고리 선택 이벤트
        val categoryClickListener = View.OnClickListener { v ->
            if (v is TextView) showCategory(v)
        }

        binding.categoryStudyTv.setOnClickListener(categoryClickListener)
        binding.categoryBookTv.setOnClickListener(categoryClickListener)
        binding.categoryMeditationTv.setOnClickListener(categoryClickListener)
        binding.categorySleepTv.setOnClickListener(categoryClickListener)
        binding.categoryEatTv.setOnClickListener(categoryClickListener)
        binding.categoryInstrumentTv.setOnClickListener(categoryClickListener)
        binding.categoryExerciseTv.setOnClickListener(categoryClickListener)
        binding.categoryDiaryText.setOnClickListener(categoryClickListener)
        binding.categoryCustomTv.setOnClickListener {
            binding.customCategoryEt.visibility = View.VISIBLE
            showCategory(binding.categoryCustomTv)
        }

        // 다음 버튼 클릭
        binding.goalSelectNextBtn.setOnClickListener {
            if (binding.goalSelectTogetherTv.isSelected) {
                if (!isCategory) {
                    binding.categoryErrorTv.setText(R.string.error_category)
                    return@setOnClickListener
                }

                val selectedCategoryEnum = categoryMap[category] ?: "SELF"
                Log.d("GoalSelectFragment", "선택된 카테고리: $selectedCategoryEnum")
                val commonGoalFragment = CommonGoalFragment()
                commonGoalFragment.arguments = Bundle().apply{
                    Log.d("GoalSelectFragment", "이름 전달 $nickname")
                    putString("goalOwnerName", nickname)
                    putString("selectedCategory", selectedCategoryEnum)
                    putString("goalType","COMMUNITY")
                }

                (context as GoalActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.goal_container, commonGoalFragment)
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

    // 선택된 카테고리 보여주는 함수
    private fun showCategory(selected: TextView) {
        val select = ContextCompat.getColor(context, R.color.white_font)
        val unselect = ContextCompat.getColor(context, R.color.blue_200)

        if (!isCategory) {
            category = selected
            isCategory = true
        } else {
            category.isSelected = false
            category.setTextColor(unselect)
            if (category == binding.categoryCustomTv && selected != binding.categoryCustomTv) {
                binding.customCategoryEt.visibility = View.GONE
                binding.customCategoryEt.text = null
                category.clearFocus()
                binding.categoryErrorTv.text = null
            }
        }

        category = selected
        category.isSelected = true
        category.setTextColor(select)
        Log.d("GoalSelectFragment", "선택된 카테고리: ${category.text}")

        textListener()
    }

    private fun textListener() {
        binding.customCategoryEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.customCategoryEt.isFocused && binding.customCategoryEt.text.toString().isEmpty()) {
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
