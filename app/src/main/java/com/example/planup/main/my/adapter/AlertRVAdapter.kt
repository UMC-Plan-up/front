package com.example.planup.main.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.databinding.ItemAlertDropdownBinding

class AlertRVAdapter(private val times: ArrayList<String>) :
    RecyclerView.Adapter<AlertRVAdapter.ViewHolder>() {

    //타이머 드롭다운의 클릭 이벤트 관리하는 인터페이스
    interface DropdownListener {
        //드롭다운에서 선택한 시간을 타이머에 적용함
        fun setTime(position: Int)
    }

    lateinit var myDropdownListener: DropdownListener
    fun setDropdownListener(dropdownListener: DropdownListener) {
        this.myDropdownListener = dropdownListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertRVAdapter.ViewHolder {
        val binding = ItemAlertDropdownBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertRVAdapter.ViewHolder, position: Int) {
        holder.bind(times[position])
        holder.binding.itemAlertTv.setOnClickListener {
            myDropdownListener.setTime(position)
        }
    }

    override fun getItemCount(): Int = times.size

    inner class ViewHolder(val binding: ItemAlertDropdownBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(time: String) {
            binding.itemAlertTv.text = time
        }
    }

}