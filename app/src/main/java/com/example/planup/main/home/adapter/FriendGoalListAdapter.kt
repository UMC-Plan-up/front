package com.example.planup.main.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.ItemFriendGoalListBinding
import com.example.planup.main.goal.item.FriendGoalListResult
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

data class FriendGoalWithAchievement(
    val goalId: Int,
    val goalName: String,
    val goalType: String,
    val goalAmount: String,
    val verificationType: String,
    val goalTime: Int,
    val frequency: Int,
    val oneDose: Int,
    val totalAchievement: Int
) {
    constructor(
        goal: FriendGoalListResult,
        totalAchievement: Int?
    ) : this(
        goal.goalId,
        goal.goalName,
        goal.goalType,
        goal.goalAmount,
        goal.verificationType,
        goal.goalTime,
        goal.frequency,
        goal.oneDose,
        totalAchievement ?: 0
    )
}

class FriendGoalListAdapter(
    private val paramItems: List<FriendGoalWithAchievement>,
    private val onItemClick: (FriendGoalWithAchievement) -> Unit
) : RecyclerView.Adapter<FriendGoalListAdapter.FriendGoalViewHolder>() {
    private val items = mutableListOf<FriendGoalWithAchievement>().apply {
        addAll(paramItems)
    }
    private var showingCnt = 3
    inner class FriendGoalViewHolder(val binding: ItemFriendGoalListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pieChart = binding.friendGoalListPc
        val tvTitle = binding.friendGoalListTitleTv
        val tvNumber = binding.friendGoalListNumberTv
        val tvDescription = binding.friendGoalListDescriptionTv
        val certificationTimerBtn = binding.certificationTimerCbtn
        val certificationPhotoBtn = binding.certificationPhotoCbtn
        val supportBtn = binding.supportbtnTv
        val motivateBtn = binding.motivatebtnTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendGoalViewHolder {
        val binding = ItemFriendGoalListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendGoalViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.goalName
        holder.tvNumber.text = item.frequency.toString()
        holder.tvDescription.text = item.goalAmount

        setupDonutChart(holder.pieChart, item.totalAchievement, position)

        // ✅ 인증 타입 처리
        when (item.verificationType) {
            "PHOTO" -> {
                holder.certificationTimerBtn.visibility = View.GONE
                holder.certificationPhotoBtn.visibility = View.VISIBLE
            }
            "TIMER" -> {
                holder.certificationTimerBtn.visibility = View.VISIBLE
                holder.certificationTimerBtn.text = formatSeconds(item.goalTime)
                holder.certificationPhotoBtn.visibility = View.GONE
            }
            else -> {
                holder.certificationTimerBtn.visibility = View.VISIBLE
                holder.certificationTimerBtn.text = ""
                holder.certificationPhotoBtn.visibility = View.GONE
            }
        }

        // ✅ 클릭 이벤트
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }

        holder.supportBtn.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return if (showingCnt < items.size) showingCnt else items.size
    }

    private fun setupDonutChart(chart: PieChart, progress: Int, position: Int) {

        val entries = listOf(
            PieEntry(progress.toFloat()),
            PieEntry((100 - progress).toFloat())
        )

        val context = chart.context
        val colorRes = when (position % 3) {
            0 -> R.color.charcolor3
            1 -> R.color.charcolor1
            else -> R.color.charcolor5
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(context.getColor(colorRes), Color.parseColor("#EEEEEE"))
            valueTextColor = Color.TRANSPARENT
            sliceSpace = 2f
        }

        val pieData = PieData(dataSet)

        chart.apply {
            data = pieData
            description.isEnabled = false
            setUsePercentValues(false)
            setDrawEntryLabels(false)
            setHoleRadius(85f)
            setTransparentCircleRadius(75f)
            setDrawCenterText(true)
            centerText = "$progress%"
            setCenterTextSize(16f)
            legend.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun formatSeconds(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<FriendGoalWithAchievement>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun showAllItems() {
        showingCnt = items.size
        notifyDataSetChanged()
    }
}
