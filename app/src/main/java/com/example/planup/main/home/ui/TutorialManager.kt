package com.example.planup.main.home.ui

import androidx.fragment.app.FragmentManager
import com.example.planup.R
import com.example.planup.main.home.adapter.TutorialDialog

class TutorialManager(private val fragmentManager: FragmentManager) {

    fun startTutorial() {
        showStep1()
    }

    private fun showStep1() {
        TutorialDialog(R.layout.dialog_tutorial1) {
            showStep2()
        }.show(fragmentManager, "tutorial1")
    }

    private fun showStep2() {
        TutorialDialog(R.layout.dialog_tutorial2) {
            showStep3()
        }.show(fragmentManager, "tutorial2")
    }

    private fun showStep3() {
        TutorialDialog(R.layout.dialog_tutorial3) {
            showStep4()
        }.show(fragmentManager, "tutorial3")
    }

    private fun showStep4() {
        TutorialDialog(R.layout.dialog_tutorial4) {
            showStep5()
        }.show(fragmentManager, "tutorial4")
    }

    private fun showStep5() {
        TutorialDialog(R.layout.dialog_tutorial5) {

        }.show(fragmentManager, "tutorial5")
    }
}
