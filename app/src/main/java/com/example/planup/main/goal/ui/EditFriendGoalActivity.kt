package com.example.planup.main.goal.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.planup.R

class EditFriendGoalActivity : AppCompatActivity() {
    private var goalId: Int = 0
    private var isSolo: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_friend_goal)

        goalId = intent.getIntExtra("goalId", 0)

        if (savedInstanceState == null) {
            if(isSolo){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.edit_friend_goal_fragment_container, EditGoalCategoryFragment())
                    .commit()
            } else {
                val titleFragment = EditGoalTitleFragment()
                titleFragment.arguments = Bundle().apply { putBoolean("isSolo", isSolo) }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.edit_friend_goal_fragment_container, titleFragment)
                    .commit()
            }
        }
    }

    fun onGoalEditComplete(showDialog: Boolean) {
        val resultIntent = Intent()
        resultIntent.putExtra("SHOW_DIALOG", showDialog)
        setResult(Activity.RESULT_OK, resultIntent)
        Log.d("EditFriendGoalActivity", "Goal 수정 완료")
        finish() // EditFriendGoalActivity를 종료
    }


}