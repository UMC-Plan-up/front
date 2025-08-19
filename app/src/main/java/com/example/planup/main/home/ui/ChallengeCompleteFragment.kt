package com.example.planup.main.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentChallengeCompleteBinding

class ChallengeCompleteFragment : Fragment() {
    private var _binding: FragmentChallengeCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 상단 UI 뒤로가기 버튼
        //    (레이아웃의 실제 id에 맞게 교체: ex) ivBack, toolbarBack 등)
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 2) 시스템(하드웨어) 뒤로가기 버튼도 동일 동작
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}