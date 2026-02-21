package com.example.planup.main.goal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalCompleteBinding

class EditGoalCompleteFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditGoalCompleteBinding.inflate(inflater, container, false)

        binding.editCompleteStartBtn.setOnClickListener {
            requireActivity().finish()
        }

        return binding.root
    }

}
