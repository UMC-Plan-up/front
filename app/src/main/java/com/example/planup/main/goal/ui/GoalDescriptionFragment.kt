package com.example.planup.main.goal.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalDescriptionBinding

class GoalDescriptionFragment : Fragment() {

    private lateinit var binding: FragmentGoalDescriptionBinding
    private var isPublic = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalDescriptionBinding.inflate(inflater, container, false)

        // 버튼 이벤트 설정
        binding.btnPublic.setOnClickListener {
            isPublic = true
            updateToggleSelection()
        }

        binding.btnPrivate.setOnClickListener {
            if (isPublic) { // 공개 상태일 때만 다이얼로그 표시
                showPrivateDialog()
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 초기 상태 설정
        updateToggleSelection()

        return binding.root
    }

    private fun updateToggleSelection() {

        binding.btnPublic.isSelected = isPublic
        binding.btnPrivate.isSelected = !isPublic

        if (isPublic) {
            binding.btnPublic.setBackgroundResource(R.drawable.toggle_selected_left)
            binding.btnPublic.setTextColor(Color.WHITE)

            binding.btnPrivate.setBackgroundResource(R.drawable.toggle_selected_right)
            binding.btnPrivate.setTextColor(Color.BLACK)
        } else {
            binding.btnPublic.setBackgroundResource(R.drawable.toggle_selected_left)
            binding.btnPublic.setTextColor(Color.WHITE)

            binding.btnPrivate.setBackgroundResource(R.drawable.toggle_selected_right)
            binding.btnPrivate.setTextColor(Color.BLACK)
        }
    }

    private fun showPrivateDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_private_goal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)

        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        yesBtn.setOnClickListener {
            isPublic = false
            updateToggleSelection()
            dialog.dismiss()
        }

        dialog.show()
    }
}