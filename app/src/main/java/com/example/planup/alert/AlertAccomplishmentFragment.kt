package com.example.planup.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.FragmentFriendAlertAccomplishmentBinding

class FriendAlertAccomplishmentFragment:Fragment(),RecyclerView.Adapter<>() {
    lateinit var binding: FragmentFriendAlertAccomplishmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendAlertAccomplishmentBinding.inflate(inflater,container,false)
        return binding.root
    }
}