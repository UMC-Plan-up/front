package com.example.planup.main.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.planup.databinding.FragmentChallengeCompleteBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.goal.GoalViewModel
import com.example.planup.main.MainActivity

class ChallengeCompleteFragment : Fragment() {

    private var _binding: FragmentChallengeCompleteBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_RESULT   = "arg_result"
        private const val ARG_OPPONENT = "arg_opponent"
        private const val ARG_PENALTY  = "arg_penalty"
        private const val ARG_MY_PERCENT   = "arg_my_percent"
        private const val ARG_OPP_PERCENT  = "arg_opponent_percent"
        private const val ARG_MY_NAME      = "arg_my_name"
        private const val ARG_MY_PROFILE_RES  = "arg_my_profile_res"
        private const val ARG_OPP_PROFILE_RES = "arg_opp_profile_res"
        private const val ARG_MY_BAR_NAME  = "arg_my_bar_name"
        private const val ARG_OPP_BAR_NAME = "arg_opp_bar_name"

        fun newInstance(
            resultText: String,
            opponentName: String,
            penaltyText: String,
            myPercent: Int = 0,
            opponentPercent: Int = 0,
            myName: String = "나",
            myProfileRes: Int? = null,
            oppProfileRes: Int? = null,
            myBarName: String = myName,
            oppBarName: String = opponentName
        ): ChallengeCompleteFragment {
            return ChallengeCompleteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_RESULT, resultText)
                    putString(ARG_OPPONENT, opponentName)
                    putString(ARG_PENALTY, penaltyText)
                    putInt(ARG_MY_PERCENT, myPercent)
                    putInt(ARG_OPP_PERCENT, opponentPercent)
                    putString(ARG_MY_NAME, myName)
                    myProfileRes?.let { putInt(ARG_MY_PROFILE_RES, it) }
                    oppProfileRes?.let { putInt(ARG_OPP_PROFILE_RES, it) }
                    putString(ARG_MY_BAR_NAME, myBarName)
                    putString(ARG_OPP_BAR_NAME, oppBarName)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChallengeCompleteBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        binding.newChallengeBtn.setOnClickListener {
            val intent = Intent(context as MainActivity, GoalActivity::class.java)
//            val goalViewModel = ViewModelProvider(this).get(GoalViewModel::class.java)
//            goalViewModel.fromWhere.value = "ChallengeCompleteFragment"
            intent.putExtra("TO_CHALLENGE_FROM","ChallengeCompleteFragment")
            startActivity(intent)
        }
    }
    // 유틸: 따옴표 제거
    private fun stripQuotes(name: String?): String {
        return name?.trim('"') ?: ""
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { parentFragmentManager.popBackStack() }
            }
        )

        val resultText     = arguments?.getString(ARG_RESULT).orEmpty()
        val opponentName   = stripQuotes(arguments?.getString(ARG_OPPONENT))  // 따옴표 제거
        val penaltyText    = arguments?.getString(ARG_PENALTY).orEmpty()
        val myPercent      = (arguments?.getInt(ARG_MY_PERCENT) ?: 0).coerceIn(0, 100)
        val oppPercent     = (arguments?.getInt(ARG_OPP_PERCENT) ?: 0).coerceIn(0, 100)
        val myName         = stripQuotes(arguments?.getString(ARG_MY_NAME)).ifBlank { "나" }
        val myProfileRes   = arguments?.getInt(ARG_MY_PROFILE_RES, 0)?.takeIf { it != 0 }
        val oppProfileRes  = arguments?.getInt(ARG_OPP_PROFILE_RES, 0)?.takeIf { it != 0 }
        val myBarName      = stripQuotes(arguments?.getString(ARG_MY_BAR_NAME)).ifBlank { myName }
        val oppBarName     = stripQuotes(arguments?.getString(ARG_OPP_BAR_NAME)).ifBlank { opponentName }

        // 상단 카드
        binding.tvMyResult.text = resultText
        binding.tvMyName.text   = myName
        myProfileRes?.let { binding.ivMyProfile.setImageResource(it) }

        val opponentResult = when (resultText) { "승리" -> "패배"; "패배" -> "승리"; else -> resultText }
        binding.tvOpponentResult.text = opponentResult
        binding.tvOpponentName.text   = opponentName   // 따옴표 제거된 값
        oppProfileRes?.let { binding.ivOpponentProfile.setImageResource(it) }

        // 패널티
        binding.penaltyContentTv.text = penaltyText

        // 목표 달성률
        binding.tvMyPercent.text = "${myPercent}%"
        binding.tvOpponentPercent.text = "${oppPercent}%"

        // 하단 라벨
        binding.tvMyBarName.text     = myBarName
        binding.tvOppnentBarName.text = oppBarName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}