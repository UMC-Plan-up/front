package com.example.planup.main.goal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.planup.R

class EditFriendGoalActivity : AppCompatActivity() {
    private var goalId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_friend_goal)

        goalId = intent.getLongExtra("goalId", -1L)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, EditGoalTitleFragment())
                .commit()
        }
    }


}