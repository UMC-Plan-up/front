package com.example.planup.alert

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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