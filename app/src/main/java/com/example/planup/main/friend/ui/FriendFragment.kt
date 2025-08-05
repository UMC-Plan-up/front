package com.example.planup.main.friend.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendBinding
import com.example.planup.main.friend.adapter.FriendListsAdapter
import com.example.planup.main.friend.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getFriendList()
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val friends = response.body()?.result?.get(0)?.friendInfoSummaryList ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val adapter = FriendListsAdapter(friends)
                        binding.rvFriendList.adapter = adapter
                        binding.rvFriendList.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            } catch (e: Exception) {
                Log.e("FriendFragment", "API error: ${e.message}")
            }
        }
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