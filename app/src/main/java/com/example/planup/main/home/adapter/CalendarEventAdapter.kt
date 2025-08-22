package com.example.planup.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.main.home.ui.CalendarEvent
import java.util.Calendar
import androidx.core.graphics.toColorInt

class CalendarEventAdapter : RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder>() {

    private var events: List<CalendarEvent> = emptyList()

    // 사용할 색상 목록
    private val colors = listOf("#548DFF", "#D454FF", "#FFEB54")

    fun submitList(newEvents: List<CalendarEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.calendar_event_title_tv)
        private val subtitleText: TextView = itemView.findViewById(R.id.calendar_event_subtitle_tv)
        private val eventView: View = itemView.findViewById(R.id.calendar_event_v)

        fun bind(event: CalendarEvent, position: Int) {
            titleText.text = event.goalName
            val period = when (event.period) {
                "WEEK" -> "매주"
                "MONTH" -> "매달"
                "DAY" -> "매일"
                else -> ""
            }
            subtitleText.text = "${period} ${event.frequency}회 이상"

            // 순차적으로 색상 적용
            val color = colors[position % colors.size]
            eventView.setBackgroundColor(color.toColorInt())
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
