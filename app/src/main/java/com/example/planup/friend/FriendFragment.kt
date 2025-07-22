package com.example.planup.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.databinding.FragmentFriendRequestsBinding

class FriendFragment : Fragment() {
    lateinit var binding: FragmentFriendBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendBinding.inflate(inflater, container, false)

        clickListener()
        return binding.root
    }

    private fun clickListener(){
        binding.ivSetting.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendListsFragment())
                .commitAllowingStateLoss()
        }

        binding.ivNotification.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendRequestsFragment())
                .commitAllowingStateLoss()
        }

        binding.btnAddFriend.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendInviteFragment())
                .commitAllowingStateLoss()
        }
    }
}