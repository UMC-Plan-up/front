package com.example.planup.main.record.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBadgesBinding

class RecordBadgesFragment : Fragment(){
    lateinit var binding: FragmentRecordBadgesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentRecordBadgesBinding.inflate(inflater, container, false)

        clickListener()
        return binding.root
    }

    private fun clickListener(){
        binding.btnBack.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordFragment())
                .commitAllowingStateLoss()
        }
    }
}