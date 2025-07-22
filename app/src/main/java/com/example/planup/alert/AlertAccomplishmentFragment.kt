package com.example.planup.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.FragmentAlertAccomplishmentBinding

class AlertAccomplishmentFragment:Fragment() {
    lateinit var binding: FragmentAlertAccomplishmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertAccomplishmentBinding.inflate(inflater,container,false)
        return binding.root
    }
}