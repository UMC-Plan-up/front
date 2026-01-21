package com.example.planup.network.controller

import com.example.planup.main.goal.adapter.MyGoalListDtoAdapter
import com.example.planup.network.getRetrofit
import com.example.planup.network.port.GoalPort
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class GoalController {
    private lateinit var myGoalListDtoAdapter: MyGoalListDtoAdapter
    fun setMyGoalListAdapter(adapter: MyGoalListDtoAdapter){
        this.myGoalListDtoAdapter = adapter
    }

    // 나의 목표 리스트 조회
//    fun fetchMyGoals(){
//        val service = getRetrofit().create(GoalPort::class.java)
//        service.getMyGoals().enqueue(object: Callback<ApiResponseListMyGoalListDto>{
//            override fun onResponse(
//                call: Call<ApiResponseListMyGoalListDto>,
//                response: Response<ApiResponseListMyGoalListDto>
//            ){
//                if(response.isSuccessful && response.body() != null){
//                    val body = response.body()!!
//                    if(body.isSuccess){
//                        myGoalListDtoAdapter.successMyGoals(body.result)
//                    }else{
//                        myGoalListDtoAdapter.failMyGoals(body.message)
//                    }
//                }else{
//                    val err = response.errorBody()?.string().orEmpty()
//                    Log.e("GoalConbtroller", "HTTP ${response.code()} $err")
//                    myGoalListDtoAdapter.failMyGoals(
//                        if(err.isNotBlank()) err else "Failed to load goals"
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponseListMyGoalListDto>, t: Throwable){
//                Log.e("GoalController", "Network error", t)
//                myGoalListDtoAdapter.failMyGoals(t.message ?: "Network error")
//            }
//        })
//    }
}