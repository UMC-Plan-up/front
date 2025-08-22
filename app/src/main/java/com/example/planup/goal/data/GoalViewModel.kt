package com.example.planup.goal.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalViewModel(): ViewModel(){
    var fromWhere = MutableLiveData<String>()
}
