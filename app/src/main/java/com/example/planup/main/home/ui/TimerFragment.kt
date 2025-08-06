package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentTimerBinding
import com.example.planup.main.home.adapter.FriendTimerAdapter
import com.example.planup.main.home.data.FriendTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private var timerJob: Job? = null
    private var isRunning = false
    private var elapsedSeconds = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedDate = arguments?.getString("selectedDate")
        val dateTv = binding.goalListTextDateTv
        val formattedDate = selectedDate?.replace("-", ".")
        dateTv.text = formattedDate

        setupSpinner()
        setupCameraPopup()
        setupTimerButton()

        val recyclerView = binding.friendTimerRv

        val examplefriendList = listOf(
            FriendTimer("닉네임", "00:00:00", R.drawable.ic_launcher_background),
            FriendTimer("닉네임", "00:00:00", R.drawable.ic_launcher_background),
            FriendTimer("닉네임", "00:00:00", R.drawable.ic_launcher_background)
        )

        val adapter = FriendTimerAdapter(examplefriendList)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.goalListSpinner

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.goal_list_spinner_dropdown,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "선택: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupCameraPopup() {
        val cameraImageView = binding.goalListBtnCameraIb

        cameraImageView.setOnClickListener {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.popup_goal_list_camera, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(null)
            popupWindow.isOutsideTouchable = true
            popupWindow.elevation = 10f

            val takePhoto = popupView.findViewById<TextView>(R.id.take_photo_tv)
            val chooseGallery = popupView.findViewById<TextView>(R.id.choose_gallery_tv)

            takePhoto.setOnClickListener {
                Toast.makeText(requireContext(), "사진 찍기 선택", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            chooseGallery.setOnClickListener {
                Toast.makeText(requireContext(), "앨범에서 선택하기 선택", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(cameraImageView, 0, 10)
        }
    }

    private fun setupTimerButton() {
        val playButton = binding.goalListPlayBtn

        playButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }
    }

    private fun startTimer() {
        isRunning = true
        //binding.goalListPlayBtn.setImageResource(R.drawable.ic_pause_circle) // 재생 → 일시정지 아이콘

        timerJob = lifecycleScope.launch {
            while (true) {
                delay(1000)
                elapsedSeconds++
                updateTimerText()
            }
        }
    }

    private fun stopTimer() {
        isRunning = false
        binding.goalListPlayBtn.setImageResource(R.drawable.ic_play_circle) // 일시정지 → 재생 아이콘
        timerJob?.cancel()
    }

    private fun updateTimerText() {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        binding.goalListTextTimerTv.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerJob?.cancel()
    }
}
