package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentAlertChallengeBinding

class AlertChallengeFragment:Fragment() {
    lateinit var binding: FragmentAlertChallengeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertChallengeBinding.inflate(inflater,container,false)
        return binding.root
    }
}