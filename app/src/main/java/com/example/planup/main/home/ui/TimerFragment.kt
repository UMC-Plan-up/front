package com.example.planup.main.home.ui

import android.R
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.databinding.FragmentTimerBinding
import com.example.planup.databinding.PopupGoalListCameraBinding
import com.example.planup.main.home.adapter.FriendTimerAdapter
import com.example.planup.main.home.data.HomeTimer
import com.example.planup.main.home.ui.viewmodel.CameraEvent
import com.example.planup.main.home.ui.viewmodel.TimerViewModel
import com.example.planup.network.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel: TimerViewModel by viewModels()
    private var selectedSpinnerItem = 0

//    private val pickImageLauncher = registerForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { handlePickedImage(it) }
//    } //갤러리 런처
//
//    private val requestCameraPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) launchCamera()
//            else Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//        } //카메라 런처

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.goalListBtnCameraIb.setOnClickListener {
            viewModel.onCameraButtonClicked()
        }
        //observeCameraEvents()

        viewModel.loadGoals(
            onCallBack = { result ->
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("setupspinner","${viewModel.goals.value}")
                        setupSpinner(viewModel.goals.value)
                    }
                    is ApiResult.Error -> {
                        Log.d("loadGoals", "Error: ${result.message}")
                    }

                    is ApiResult.Exception -> {
                        Log.d("loadGoals", "Exception: ${result.error}")
                    }

                    is ApiResult.Fail -> {
                        Log.d("loadGoals", "Fail: ${result.message}")
                    }

                    else -> {}
                }
            }
        )

        lifecycleScope.launch {
            viewModel.goals.collect { goals ->
                // spinner 세팅
            }
        }

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
                binding.goalListTextTimerTv.text = time
            }
        }

        binding.goalListPlayBtn.setOnClickListener {
            val goalId = viewModel.selectedGoalId.value
            viewModel.startTimer(goalId,
                onCallBack = { result ->
                    when (result) {
                        is ApiResult.Error -> {
                            Log.d("startTimer", "Error: ${result.message}")
                        }

                        is ApiResult.Exception -> {
                            Log.d("startTimer", "Exception: ${result.error}")
                        }

                        is ApiResult.Fail -> {
                            Log.d("startTimer", "Fail: ${result.message}")
                        }

                        else -> {}
                    }
                })
        }

        binding.editMemo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val goalId = viewModel.selectedGoalId.value
                viewModel.saveMemo(goalId, "2025-10-19", binding.editMemo.text.toString(),
                    onCallBack = { result ->
                        when (result) {
                            is ApiResult.Error -> {
                                Log.d("saveMemo", "Error: ${result.message}")
                            }
                            is ApiResult.Exception -> {
                                Log.d("saveMemo", "Exception: ${result.error}")
                            }
                            is ApiResult.Fail -> {
                                Log.d("saveMemo", "Fail: ${result.message}")
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }

    fun setupSpinner(events: List<HomeTimer>){
        Log.d("setupSpinner", "events: $events")
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

                // 1️⃣ 기존 타이머 종료
//                if (viewModel.isRunning) {
//                    stopTimer()
//                } else {
//                    val timerPrefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
//                    timerPrefs.edit().remove("timerId").apply()
//                }

                // 2️⃣ 새로 선택된 목표
                val selectedGoal = events[position]
                selectedSpinnerItem = selectedGoal.goalId
                viewModel.selectGoal(selectedSpinnerItem)

                // 3️⃣ UI 초기화
                viewModel.loadTotalTime(selectedSpinnerItem,
                    onCallBack = { result ->
                        when (result) {
                            is ApiResult.Error -> {
                                Log.d("loadTotalTime", "Error: ${result.message}")
                            }
                            is ApiResult.Exception -> {
                                Log.d("loadTotalTime", "Exception: ${result.error}")
                            }
                            is ApiResult.Fail -> {
                                Log.d("loadTotalTime", "Fail: ${result.message}")
                            }
                            else -> {}
                        }
                    })
                binding.goalListTextTimerTv.text = viewModel.timerText.value

                // 4️⃣ 새 타이머 시작
                //startTimer(token, selectedGoal.goalId)

                // 5️⃣ 기타 UI 로드
                //setFriendsTimer()
                viewModel.loadFriends(selectedSpinnerItem,
                    onCallBack = { result ->
                        when (result) {
                            is ApiResult.Error -> {
                                Log.d("loadFriends", "Error: ${result.message}")
                            }
                            is ApiResult.Exception -> {
                                Log.d("loadFriends", "Exception: ${result.error}")
                            }
                            is ApiResult.Fail -> {
                                Log.d("loadFriends", "Fail: ${result.message}")
                            }
                            else -> {}
                        }
                    })
                setGoalInfo()
                viewModel.loadMemo(selectedSpinnerItem, viewModel.selectedDate,
                    onCallBack = { result ->
                        when (result) {
                            is ApiResult.Error -> {
                                Log.d("loadMemo", "Error: ${result.message}")
                            }
                            is ApiResult.Exception -> {
                                Log.d("loadMemo", "Exception: ${result.error}")
                            }
                            is ApiResult.Fail -> {
                                Log.d("loadMemo", "Fail: ${result.message}")
                            }
                            else -> {}
                        }
                    })
                setupTimerButton(selectedSpinnerItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

//    private fun setFriendsTimer() {
//        viewModel.loadFriends(viewModel.selectedGoalId.value, token)
//        val adapter = FriendTimerAdapter(viewModel.friends.value)
//        binding.friendTimerRv.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.friendTimerRv.adapter = adapter
//    }

    private fun setGoalInfo() {
        viewModel.loadGoalInfo(viewModel.selectedGoalId.value,
            onCallBack = { result ->
                when (result) {
                    is ApiResult.Error -> {
                        Log.d("loadGoalInfo", "Error: ${result.message}")
                    }
                    is ApiResult.Exception -> {
                        Log.d("loadGoalInfo", "Exception: ${result.error}")
                    }
                    is ApiResult.Fail -> {
                        Log.d("loadGoalInfo", "Fail: ${result.message}")
                    }
                    else -> {}
                }
                })
        binding.timerGoalAmountTv.text = viewModel.goalAmount
        binding.timerGoalFrequencyTv.text = "${viewModel.goalFreq}회 이상"
    }

    private fun setupTimerButton(goalId: Int) {
        val playButton = binding.goalListPlayBtn
        playButton.setOnClickListener {
            viewModel.clickTimerButton(goalId)
        }
    }

//    private fun observeCameraEvents() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
//                viewModel.cameraEvent.collect { event ->
//                    when (event) {
//                        is CameraEvent.ShowCameraPopup -> showCameraPopup()
//                        is CameraEvent.OpenCamera -> launchCameraOrRequestPermission()
//                        is CameraEvent.OpenGallery -> pickImageLauncher.launch("image/*")
//                    }
//                }
//            }
//        }
//    }

    private fun showCameraPopup() {
        val popupView = layoutInflater.inflate(com.example.planup.R.layout.popup_goal_list_camera, null)
        val popupBinding = PopupGoalListCameraBinding.bind(popupView)

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable())
            isOutsideTouchable = true
            elevation = 10f
        }

//        popupBinding.takePhotoTv.setOnClickListener {
//            popupWindow.dismiss()
//            tempGoalId = selectedSpinnerItem
//            viewModel.onPickCamera()
//        }
//
//        popupBinding.chooseGalleryTv.setOnClickListener {
//            popupWindow.dismiss()
//            tempGoalId = selectedSpinnerItem
//            viewModel.onPickGallery()
//        }

        popupWindow.showAsDropDown(binding.goalListBtnCameraIb, 0, 10)
    }



}
