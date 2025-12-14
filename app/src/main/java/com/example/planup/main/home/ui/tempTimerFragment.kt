//package com.example.planup.main.home.ui
//
//import android.Manifest
//import android.content.Context
//import android.content.Context.MODE_PRIVATE
//import android.content.SharedPreferences
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.PopupWindow
//import android.widget.Spinner
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.planup.R
//import com.example.planup.databinding.FragmentTimerBinding
//import com.example.planup.databinding.PopupGoalListCameraBinding
//import com.example.planup.main.home.adapter.FriendTimerAdapter
//import com.example.planup.main.home.data.FriendTimer
//import com.example.planup.network.RetrofitInstance
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import com.example.planup.main.goal.item.GoalRetrofitInstance
//import com.example.planup.main.home.data.HomeTimer
//import com.example.planup.main.goal.item.GoalApiService
//import com.example.planup.main.goal.item.MemoRequest
//import retrofit2.HttpException
//import java.time.LocalDate
//import kotlin.collections.plus
//import android.view.inputmethod.InputMethodManager
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.core.content.edit
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import java.io.File
//
//class TimerFragment : Fragment() {
//    private lateinit var prefs: SharedPreferences
//    private var selectedSpinnerItem = 0
//
//    private lateinit var binding: FragmentTimerBinding
//    private var timerJob: Job? = null
//    private var isRunning = false
//    private var elapsedSeconds = 0
//    private var events = listOf<HomeTimer>()
//    private lateinit var selectedDate: String
//    private var photoUri: Uri? = null
//    private var tempGoalId: Int = 0
//    private var tempLoadedGoalId: Int = 0
//
    // 카메라 런처
//    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            photoUri?.let { uploadImage(it, tempGoalId) }
//        }
//    }
//    // 갤러리 런처
//    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        uri?.let { uploadImage(it, tempGoalId) }
//    }
//    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//        if (granted) {
//            launchCamera()
//        } else {
//            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        binding = FragmentTimerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        selectedDate = arguments?.getString("selectedDate") ?: LocalDate.now().toString()
//        val dateTv = binding.goalListTextDateTv
//
//        val formattedDate = selectedDate.replace("-", ".")
//        prefs = requireContext().getSharedPreferences("userInfo", 0)
//        val token = prefs.getString("accessToken", null)
//
//        dateTv.text = formattedDate
//
//        loadMyGoalList(token)
//        setupCameraPopup()
//
//        val recyclerView = binding.friendTimerRv
//
//        val examplefriendList = listOf(
//            FriendTimer("닉네임", "00:00:00", null),
//            FriendTimer("닉네임", "00:00:00", null),
//            FriendTimer("닉네임", "00:00:00", null)
//        )
//
//        val adapter = FriendTimerAdapter(examplefriendList)
//
//        recyclerView.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        recyclerView.adapter = adapter
//
//        val backBtn = binding.goalListBackBtn
//        backBtn.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }
//        binding.editMemo.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {  // 포커스를 잃었을 때
//                Log.d("editMemo", "포커스 잃음")
//                val memoText = binding.editMemo.text.toString()
//                val request = MemoRequest(
//                    memo = memoText,
//                    memoDate = selectedDate,
//                    empty = memoText.isEmpty(),
//                    trimmedMemo = memoText.trim()
//                )
//                postMemo(token, selectedSpinnerItem, selectedDate, request)
//            }
//        }
//        binding.root.setOnClickListener {
//            binding.editMemo.clearFocus()
//
//            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(binding.editMemo.windowToken, 0)
//        }
//
//        binding.achievementStatusMcv.setOnClickListener {
//            val selectedGoalId = selectedSpinnerItem
//
//        }
//
//    }
//
//    private fun setupSpinner(token: String?, events: List<HomeTimer>) {
//        Log.d("setupSpinner","$events")
//        val spinner: Spinner = binding.goalListSpinner
//        val goalNames = events.map { it.goalName }
//        val adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            goalNames
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//
//                // 1️⃣ 기존 타이머 종료
//                if (isRunning) {
//                    stopTimer()
//                } else {
//                    val timerPrefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
//                    timerPrefs.edit().remove("timerId").apply()
//                }
//
//                // 2️⃣ 새로 선택된 목표
//                val selectedGoal = events[position]
//                selectedSpinnerItem = selectedGoal.goalId
//
//                // 3️⃣ UI 초기화
//                binding.goalListTextTimerTv.text = "00:00:00"
//                elapsedSeconds = 0
//
//                // 4️⃣ 새 타이머 시작
//                //startTimer(token, selectedGoal.goalId)
//
//                // 5️⃣ 기타 UI 로드
//                loadTodayTotalTime(token, selectedGoal.goalId)
//                loadFriendsTimer(token, selectedGoal.goalId)
//                loadGoalInfo(token, selectedGoal.goalId)
//                loadDateMemo(token, selectedGoal.goalId, selectedDate)
//
//                setupTimerButton(token, selectedGoal.goalId)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//    }
//
//    private fun startTimerWithSavedId(token: String?, goalId: Int, timerId: Int) {
//        isRunning = true
//        //binding.goalListPlayBtn.setImageResource(R.drawable.ic_pause_circle)
//
//        timerJob?.cancel()
//        timerJob = lifecycleScope.launch {
//            while (true) {
//                delay(1000)
//                elapsedSeconds++
//                updateTimerText()
//            }
//        }
//    }
//
//    private fun setupCameraPopup() {
//        val cameraImageView = binding.goalListBtnCameraIb
//
//        cameraImageView.setOnClickListener {
//            val inflater = LayoutInflater.from(requireContext())
//            val popupView = inflater.inflate(R.layout.popup_goal_list_camera, null)
//            val popupBinding = PopupGoalListCameraBinding.bind(popupView)
//
//            val popupWindow = PopupWindow(
//                popupBinding.root,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                true
//            )
//
//            popupWindow.setBackgroundDrawable(null)
//            popupWindow.isOutsideTouchable = true
//            popupWindow.elevation = 10f
//
//            val takePhoto = popupBinding.takePhotoTv
//            val chooseGallery = popupBinding.chooseGalleryTv
//
//            takePhoto.setOnClickListener {
//                popupWindow.dismiss()
//                tempGoalId = selectedSpinnerItem
//                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED) {
//                    launchCamera()
//                } else {
//                    requestCameraPermission.launch(Manifest.permission.CAMERA)
//                }
//            }
//
//            chooseGallery.setOnClickListener {
//                popupWindow.dismiss()
//                tempGoalId = selectedSpinnerItem
//                pickImageLauncher.launch("image/*")
//            }
//
//            popupWindow.showAsDropDown(cameraImageView, 0, 10)
//        }
//    }
//
//    private fun launchCamera() {
//        val photoFile = File(requireContext().cacheDir, "temp_photo.jpg")
//        photoUri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().packageName}.provider",
//            photoFile
//        )
//        takePhotoLauncher.launch(photoUri)
//    }
//    private fun setupTimerButton(token: String?, goalId: Int) {
//        val playButton = binding.goalListPlayBtn
//        Log.d("TimerFragment", "setupTimerButton goalId: $goalId")
//        playButton.setOnClickListener {
//            if (isRunning) {
//                stopTimer()
//            } else {
//                startTimer(token, goalId)
//            }
//        }
//    }
//
//    private fun startTimer(token: String?, goalId: Int) {
//        isRunning = true
//        timerJob?.cancel()
//
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitInstance.verificationApi.postTimerStart(token = "Bearer $token", goalId = goalId)
//                if (response.isSuccess) {
//                    Log.d("startTimer", "타이머 시작 성공")
//                    val timerId = response.result.timerId
//                    val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
//                    prefs.edit().putInt("timerId", timerId).apply()
//                    startTimerWithSavedId(token, goalId, timerId)
//                    Log.d("startTimer", "타이머 시작 성공, timerId: $timerId")
//                } else {
//                    Log.e("startTImer", "타이머 시작 실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                if(e is HttpException) Log.e("startTimer", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                else Log.e("startTimer", "Other error: ${e.message}", e)
//            }
//        }
//    }
//
//    private fun stopTimer() {
//        if (!isRunning) return
//        isRunning = false
//        binding.goalListPlayBtn.setImageResource(R.drawable.ic_play_circle)
//        timerJob?.cancel()
//
//        val token = prefs.getString("accessToken", null)
//        stopTimerApi(token = "Bearer $token", goalId = selectedSpinnerItem)
//
//        // 🔹 SharedPreferences에 저장된 timerId 제거
//        val timerPrefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
//        timerPrefs.edit().remove("timerId").apply()
//    }
//
//    private fun stopTimerApi(token: String?, goalId: Int) {
//        val prefs = requireContext().getSharedPreferences("timerPrefs", MODE_PRIVATE)
//        val timerId = prefs.getInt("timerId", -1)
//        Log.d("stoptimerapi", "timerId: $timerId")
//
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitInstance.verificationApi.putTimerStop(token = "Bearer $token", timerId)
//                if (response.isSuccess) {
//                    Log.d("stoptimerapi", "타이머 중지 성공")
//                    isRunning = false
//                } else {
//                    Log.e("stoptimerapi", "타이머 중지 실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                if (e is HttpException) {
//                    Log.e("stoptimerapi", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                } else {
//                    Log.e("stoptimerapi", "Other error: ${e.message}", e)
//                }
//            }
//        }
//    }
//
//    private fun updateTimerText() {
//        val hours = elapsedSeconds / 3600
//        val minutes = (elapsedSeconds % 3600) / 60
//        val seconds = elapsedSeconds % 60
//        binding.goalListTextTimerTv.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//    }
//
//    private fun loadTodayTotalTime(token: String?, goalId: Int) {
//        lifecycleScope.launch {
//            try {
//                val response = RetrofitInstance.verificationApi.getTodayTotalTime(token = "Bearer $token", goalId = goalId)
//                if (response.isSuccess) {
//                    val formattedTime = response.result.formattedTime
//                    binding.goalListTextTimerTv.text = formattedTime
//                    Log.d("loadTotalTime", "오늘 총 시간: $formattedTime")
//                } else {
//                    Log.e("loadTotalTime", "실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                if (e is HttpException) {
//                    Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                } else {
//                    Log.e("API", "Other error: ${e.message}", e)
//                }
//            }
//        }
//    }
//
//    private fun isVerified(token: String?, goalId: Int) {
//        lifecycleScope.launch {
//            try {
//                val goalService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = goalService.getEditGoal(token = "Bearer token", goalId = goalId)
//                if(response.isSuccess) {
//                    val verificationType = response.result.verificationType
//                    if(verificationType == "PHOTO") {
//                        val photoResponse = goalService.getGoalPhotos(token = "Bearer $token", goalId = goalId)
//                        if(photoResponse.isSuccess) {
//                            val uri = photoResponse.result.firstOrNull()?.photoImg
//                            if(uri != null) {
//                                binding.achievementStatusIv.setImageResource(R.drawable.ic_achievement_status_check)
//                                Toast.makeText(requireContext(), "인증이 확인되었어요.", Toast.LENGTH_SHORT).show()
//                            } else {
//                                binding.achievementStatusIv.setImageResource(R.drawable.ic_achivement_status)
//                                Toast.makeText(requireContext(), "사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Log.d("isVerified", "사진 불러오기 실패: ${photoResponse.message}")
//                        }
//                    } else if (verificationType == "TIMER"){
//
//
//                    } else {
//
//                    }
//                } else {
//
//                }
//            } catch (e: Exception) {
//
//            }
//        }
//
//    }
//
//
//
//    private fun loadFriendsTimer(token: String?, goalId: Int) {
//        lifecycleScope.launch {
//            try {
//                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = apiService.getFriendsTimer(
//                    token = "Bearer $token",
//                    goalId = goalId
//                )
//                if (response.isSuccess) {
//                    val friendList = response.result.map { friend ->
//                        FriendTimer(
//                            nickname = friend.nickname,
//                            time = friend.todayTime,
//                            profileResId = friend.profileImg
//                        )
//                    }
//
//                    val adapter = FriendTimerAdapter(friendList)
//                    binding.friendTimerRv.layoutManager =
//                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//                    binding.friendTimerRv.adapter = adapter
//
//                    Log.d("TimerFragmentAPI", "친구 타이머 불러오기 성공: ${friendList.size}명")
//                } else {
//                    Log.e("TimerFragmentAPI", "친구 타이머 불러오기 실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                if (e is HttpException) {
//                    Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                } else {
//                    Log.e("API", "Other error: ${e.message}", e)
//                }
//            }
//        }
//    }
//
//    private fun loadMyGoalList(token: String?) {
//        if(token == null) {
//            Log.d("TimerFragment", "loadMyGoalList token null")
//            return
//        }
//        lifecycleScope.launch {
//            try {
//                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = apiService.getMyGoalList(token = "Bearer $token")
//                if (response.isSuccess) {
//                    val goals = response.result
//                    Log.d("TimerFragment", "loadMyGoalList success: $goals")
//                    for (goal in goals) {
//                        events+=HomeTimer(goal.goalId, goal.goalName)
//                    }
//                    setupSpinner(token, events)
//                } else {
//                    Log.d("timer_loadgoal","실패")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                if (e is HttpException) {
//                    Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                } else {
//                    Log.e("API", "Other error: ${e.message}", e)
//                }
//            }
//        }
//    }
//
//    private fun loadGoalInfo(token: String?, goalId: Int){
//        lifecycleScope.launch {
//            try {
//                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = apiService.getEditGoal(token = "Bearer $token", goalId = goalId)
//                if (response.isSuccess) {
//                    val goalData = response.result
//                    val frequency = goalData.frequency.toString()
//                    binding.timerGoalAmountTv.text = goalData.goalAmount
//                    binding.timerGoalFrequencyTv.text = "${frequency}회 이상"
//
//                } else {
//                    Log.d("EditGoalTitleFragment", "API 실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("EditGoalTitleFragment", "네트워크 오류 Exception: $e")
//            }
//        }
//    }
//
//    private fun loadDateMemo(token: String?, goalId: Int, date: String){
//        lifecycleScope.launch {
//            try {
//                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = apiService.getDateMemo(token = "Bearer $token", goalId = goalId, date = date)
//                if(response.isSuccess){
//                    val result = response.result
//                    if(result.exists) binding.editMemo.setText(result.memo)
//                    else binding.editMemo.setText("")
//                } else {
//                    Log.d("loadDateMemo", "메모 불러오기 실패")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//    }
//
//    private fun postMemo(token: String?, goalId: Int, date: String, request: MemoRequest){
//        lifecycleScope.launch {
//            try {
//                val apiService = GoalRetrofitInstance.api.create(GoalApiService::class.java)
//                val response = apiService.saveMemo(
//                    token = "Bearer $token",
//                    goalId = goalId,
//                    request = request
//                )
//
//                if (response.isSuccess) {
//                    Log.d("postMemo", "메모 저장 성공")
//                } else {
//
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//
//            }
//        }
//
//    }
//
//    private fun uploadImage(uri: Uri, goalId: Int ) {
//        val contentResolver = requireContext().contentResolver
//        val inputStream = contentResolver.openInputStream(uri) ?: return
//        val file = File(requireContext().cacheDir, "upload_photo.jpg")
//        inputStream.use { input ->
//            file.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData("photoFile", file.name, requestFile)
//        val token = prefs.getString("accessToken",null)
//        lifecycleScope.launch {
//            try {
//                val service = RetrofitInstance.verificationApi
//                val response = service.uploadPhoto(
//                    token = "Bearer $token", // 실제 토큰
//                    goalId = goalId, // 실제 goalId
//                    photoFile = body
//                )
//
//                if (response.isSuccess) {
//                    Log.d("uploadImage", "사진 업로드 성공")
//                } else {
//                    Log.d("uploadImage", "사진 업로드 실패: ${response.message}")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                if(e is HttpException) Log.e("API", "Http error: ${e.code()} ${e.response()?.errorBody()?.string()}")
//                else Log.e("API", "Other error: ${e.message}", e)
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (isRunning) {
//            stopTimer()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        if (isRunning) {
//            stopTimer()
//        }
//        timerJob?.cancel()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (isRunning) {
//            stopTimer()
//        }
//    }
//}
