package com.example.planup.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentAlertReactionBinding

class AlertReactionFragment:Fragment() {
    lateinit var binding: FragmentAlertReactionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertReactionBinding.inflate(inflater,container,false)

        return binding.root
    }
}