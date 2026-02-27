package com.planup.planup.goal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.planup.planup.databinding.FragmentTopTitleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopTitleFragment : Fragment() {

    lateinit var binding: FragmentTopTitleBinding
    var title: String? = null
    var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("user") ?: "사용자"
        mode = arguments?.getString("mode")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기
        binding.backIcon.setOnClickListener { parentFragmentManager.popBackStack() }
    }

}