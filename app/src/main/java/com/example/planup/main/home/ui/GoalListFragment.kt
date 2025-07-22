package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentGoalListBinding
import android.widget.TextView
import com.example.planup.R

class GoalListFragment : Fragment() {

    private lateinit var binding: FragmentGoalListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.goalListSpinner

        ArrayAdapter.createFromResource(
            requireContext(),                     // context: Fragment라면 requireContext()
            R.array.goal_list_spinner_dropdown, // string-array name
            android.R.layout.simple_spinner_item // 기본 항목 레이아웃
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 드롭다운 레이아웃
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

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택되지 않았을 때
            }
        }

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

            // 배경 클릭 시 닫히게 하기
            popupWindow.setBackgroundDrawable(null)
            popupWindow.isOutsideTouchable = true
            popupWindow.elevation = 10f

            // 클릭 이벤트 처리
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

            // 위치 조정: anchor 바로 아래에 띄우기
            popupWindow.showAsDropDown(cameraImageView, 0, 10)
        }
    }
}