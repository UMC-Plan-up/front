package com.example.planup.main.goal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalViewModel(): ViewModel(){
    var fromWhere = MutableLiveData<String>()
}