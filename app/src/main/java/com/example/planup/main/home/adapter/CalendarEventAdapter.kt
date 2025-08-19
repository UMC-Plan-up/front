package com.example.planup.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R

class CalendarEventAdapter : RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder>() {

    private var events: List<String> = emptyList()

    // 사용할 색상 목록
    private val colors = listOf("#548DFF", "#D454FF", "#FFEB54")

    fun submitList(newEvents: List<String>) {
        events = newEvents
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val eventView: View = itemView.findViewById(R.id.calendar_event_v)

        fun bind(event: String, position: Int) {
            titleText.text = event

            // 순차적으로 색상 적용
            val color = colors[position % colors.size]
            eventView.setBackgroundColor(android.graphics.Color.parseColor(color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_event, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], position)
    }
}
