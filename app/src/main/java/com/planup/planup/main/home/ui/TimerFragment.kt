package com.planup.planup.main.home.ui

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.planup.planup.databinding.FragmentTimerBinding
import com.planup.planup.main.goal.item.MyGoalListItem
import com.planup.planup.main.home.adapter.FriendTimerAdapter
import com.planup.planup.main.home.ui.viewmodel.TimerViewModel
import com.planup.planup.network.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel: TimerViewModel by viewModels()
    private var selectedSpinnerItem = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.goalListBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        initClickListener()
        observeViewModel()

        viewModel.loadGoals(createErrorHandler("loadGoals_init") {
            setupSpinner(viewModel.goals.value)
        })

        lifecycleScope.launch {
            viewModel.friends.collect { friends ->
                binding.friendTimerRv.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = FriendTimerAdapter(friends)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.timerText.collect { time ->
                binding.timerMainTv.text = time
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                binding.timerDateTv.text = date
            }
        }

        binding.goalListPlayBtn.setOnClickListener {
            if(viewModel.isRunning.value) viewModel.stopTimer(createErrorHandler("stopTimer"))
            else viewModel.startTimer(createErrorHandler("startTimer"))
        }

        binding.editMemo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.saveMemo(binding.editMemo.text.toString(),
                    createErrorHandler("saveMemo")
                )
            }
        }
    }

    fun setupSpinner(events: List<MyGoalListItem>){
        val spinner: Spinner = binding.goalListSpinner
        val goalNames = events.map { it.goalName }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            goalNames
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                // 2️⃣ 새로 선택된 목표
                val selectedGoal = events[position]
                selectedSpinnerItem = selectedGoal.goalId
                viewModel.selectGoal(selectedSpinnerItem)

                // 3️⃣ UI 초기화
                viewModel.loadTotalTime(createErrorHandler("loadTotalTime"))
                binding.timerMainTv.text = viewModel.timerText.value

                // 4️⃣ 새 타이머 시작
                //startTimer(token, selectedGoal.goalId)

                // 5️⃣ 기타 UI 로드
                //setFriendsTimer()
                viewModel.loadFriends(createErrorHandler("loadFriends"))
                setGoalInfo()
                viewModel.loadMemo(createErrorHandler("loadMemo"){
                    binding.editMemo.setText(viewModel.memo.value)
                    Log.d("setText", viewModel.memo.value)
                })
                setupTimerButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setGoalInfo() {
        viewModel.loadGoalInfo(createErrorHandler("loadGoalInfo"))
        binding.timerGoalAmountTv.text = viewModel.goalAmount
        binding.timerGoalFrequencyTv.text = "${viewModel.goalFreq}회 이상"
    }

    private fun setupTimerButton() {
        val playButton = binding.goalListPlayBtn
        playButton.setOnClickListener {
            viewModel.clickTimerButton()
        }
    }

    private fun initClickListener() {
        binding.goalListBtnCameraIb.setOnClickListener {
                val fragment = PhotoManageFragment.newInstance(
                    viewModel.selectedGoalId.value,
                    viewModel.selectedDate.value
                )
                parentFragmentManager.beginTransaction()
                    .replace(com.planup.planup.R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        binding.timerDateBeforeIv.setOnClickListener {
            viewModel.prevDay()
        }

        binding.timerDateNextIv.setOnClickListener {
            viewModel.nextDay()
        }
    }

    /* ------------------------------
       ViewModel 관찰
     ------------------------------ */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.imageUri.collect { uri ->
                uri?.let {
                    binding.goalListBtnCameraIb.setImageURI(it)
                }
            }
        }
    }

    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopTimer(createErrorHandler("stopTimer"))
    }

}
