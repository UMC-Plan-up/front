package com.example.planup.main.goal.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalViewModel(): ViewModel(){
    var fromWhere = MutableLiveData<String>()
}