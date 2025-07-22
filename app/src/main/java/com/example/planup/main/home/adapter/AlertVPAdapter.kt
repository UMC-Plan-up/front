package com.example.planup.main.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.planup.main.home.ui.AlertAccomplishmentFragment
import com.example.planup.main.home.ui.AlertChallengeFragment
import com.example.planup.main.home.ui.AlertReactionFragment

class AlertVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlertAccomplishmentFragment()
            1 -> AlertReactionFragment()
            else -> AlertChallengeFragment()
        }
    }
}