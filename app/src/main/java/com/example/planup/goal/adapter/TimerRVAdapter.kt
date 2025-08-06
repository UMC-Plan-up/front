package com.example.planup.goal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.ItemTimerDropdownBinding

//타이머 설정에서 드롭다운을 통해 시간을 설정할 수 있음
//리사이클러 뷰를 통해 구현한 드롭다운을 관리하기 위한 어댑터
class TimerRVAdapter(private val times: ArrayList<String>):RecyclerView.Adapter<TimerRVAdapter.ViewHolder>() {

    //타이머 드롭다운의 클릭 이벤트 관리하는 인터페이스
    interface DropdownListener{
        //드롭다운에서 선택한 시간을 타이머에 적용함
        fun setTime(position: Int)
    }
    lateinit var myDropdownListener: DropdownListener
    fun setDropdownListener(dropdownListener: DropdownListener){
        this.myDropdownListener = dropdownListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimerDropdownBinding.inflate(
            LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = times.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(times[position])
        holder.binding.itemTimerTv.setOnClickListener {
            myDropdownListener.setTime(position)
        }
    }

    inner class  ViewHolder(val binding: ItemTimerDropdownBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(time: String){
            binding.itemTimerTv.text = time
        }
    }
}