package com.example.planup.main.home.ui

import android.R
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.home.adapter.FriendTimerAdapter
import com.example.planup.main.home.data.HomeTimer
import com.example.planup.main.home.ui.viewmodel.CameraEvent
import com.example.planup.main.home.ui.viewmodel.TimerViewModel
import com.example.planup.network.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.time.LocalDate

@AndroidEntryPoint
class TimerFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel: TimerViewModel by viewModels()
    private var selectedSpinnerItem = 0
    private var cameraImageUri: Uri? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.setImage(it)
            }
        }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                viewModel.setImage(cameraImageUri!!)
            }
        }

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
        viewModel.preselectedDate = arguments?.getString("selectedDate").toString()

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
                binding.timerMainTv.text = time
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                binding.timerDateTv.text = date
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

    fun setupSpinner(events: List<MyGoalListItem>){
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
                binding.timerMainTv.text = viewModel.timerText.value

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
                viewModel.loadMemo(selectedSpinnerItem, viewModel.selectedDate.value,
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

    private fun initClickListener() {
        binding.goalListBtnCameraIb.setOnClickListener {
            showImagePickerBottomSheet()
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

    /* ------------------------------
       BottomSheet
     ------------------------------ */
    private fun showImagePickerBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(com.example.planup.R.layout.bottom_sheet_image_camera, null)

        view.findViewById<TextView>(com.example.planup.R.id.timer_bottom_camera_tv).setOnClickListener {
            cameraImageUri = createCameraImageUri()
            cameraLauncher.launch(cameraImageUri)
            dialog.dismiss()
        }

        view.findViewById<TextView>(com.example.planup.R.id.timer_bottom_gallery_tv).setOnClickListener {
            galleryLauncher.launch("image/*")
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    /* ------------------------------
       카메라 Uri 생성
     ------------------------------ */
    private fun createCameraImageUri(): Uri {
        val file = File(
            requireContext().cacheDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
    }

}
