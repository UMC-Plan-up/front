package com.example.planup.main.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalTimerBinding
import com.example.planup.databinding.FragmentEditGoalTitleBinding

class EditGoalTitleFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalTitleBinding

    private var goalId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // arguments에서 goalId 받아오기
        goalId = arguments?.getLong("goalId") ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalTitleBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_edit_goal_title, container, false)

        val titleEt = binding.editFriendGoalNameEt
        val oneDoseEt = binding.editFriendGoalPeriodEt
        val nextBtn = binding.editFriendGoalNextBtn
        nextBtn.setOnClickListener {
            val title = titleEt.text.toString()
            val oneDose = oneDoseEt.text.toString()
            val bundle = Bundle().apply {
                putLong("goalId", goalId)
                putString("title", title)
                putString("oneDose", oneDose)
            }

            val nextFragment = EditGoalTimerFragment()
            nextFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }
        return view
    }
}
