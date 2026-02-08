package com.example.planup.main.record.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBadgesBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.ui.FriendFragment
import com.example.planup.main.record.adapter.BadgeRow
import com.example.planup.main.record.adapter.BadgeSectionAdapter
import com.example.planup.main.record.ui.viewmodel.BadgeViewModel
import com.example.planup.main.record.ui.viewmodel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class RecordBadgesFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentRecordBadgesBinding
    private val spanCount = 4
    private val viewModel: BadgeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBadgesBinding.inflate(inflater, container, false)

        val adapter = BadgeSectionAdapter(emptyList())

        val glm = GridLayoutManager(requireContext(), spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    if (adapter.getItemViewType(position) == 0) spanCount else 1
            }
        }

        binding.rvBadges.layoutManager = glm
        binding.rvBadges.adapter = adapter

        viewModel.loadBadges()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.badges.collect { rows ->
                adapter.submitList(rows)
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        onClickListener()
        return binding.root
    }

    private fun onClickListener(){
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordFragment())
                .commitAllowingStateLoss()
        }
    }


}