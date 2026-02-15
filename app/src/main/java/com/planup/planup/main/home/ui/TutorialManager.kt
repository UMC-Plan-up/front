package com.planup.planup.main.home.ui

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.planup.planup.R
import com.planup.planup.main.home.adapter.TutorialDialog

class TutorialManager(
    private val fragmentManager: FragmentManager,
    private val context: Context
) {

    fun startTutorial() {
        showStep1()
    }

    private fun showStep1() {
        val prefs = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val nickname = prefs.getString("nickname", null).toString() // SharedPreferences에서 값 가져오기

        TutorialDialog(
            R.layout.dialog_tutorial1,
            onNextClicked = { showStep2() },
            textReplacements = mapOf(R.id.tutorial1_title_tv to nickname) // 치환할 TextView id
        ).show(fragmentManager, "tutorial1")
    }

    private fun showStep2() {
        TutorialDialog(R.layout.dialog_tutorial2, onNextClicked = { showStep3() })
            .show(fragmentManager, "tutorial2")
    }

    private fun showStep3() {
        TutorialDialog(R.layout.dialog_tutorial3, onNextClicked = { showStep4() })
            .show(fragmentManager, "tutorial3")
    }

    private fun showStep4() {
        TutorialDialog(R.layout.dialog_tutorial4, onNextClicked = { showStep5() })
            .show(fragmentManager, "tutorial4")
    }

    private fun showStep5() {
        val prefs = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val percentage = prefs.getString("nickname", null).toString()

        TutorialDialog(
            R.layout.dialog_tutorial5,
            onNextClicked = {},
            textReplacements = mapOf(R.id.tutorial3_desc_tv to percentage)
        ).show(fragmentManager, "tutorial5")
    }
}
