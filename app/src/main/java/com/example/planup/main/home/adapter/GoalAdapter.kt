package com.example.planup.main.home.adapter

import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.main.goal.item.GoalItem
import com.example.planup.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GoalAdapter(
    private val items: List<GoalItem>,
    private val onItemClick: (GoalItem) -> Unit,
    private val onDeactivateConfirmed: () -> Unit,
    private val onActivateConfirmed: () -> Unit,
    private val onDeleteConfirmed: () -> Unit) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var isEditMode: Boolean = false

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pieChart: PieChart = view.findViewById(R.id.goal_list_rv_pc)
        val title: TextView = view.findViewById(R.id.goal_list_title_tv)
        val description: TextView = view.findViewById(R.id.goal_list_description_tv)
        val deleteBtn: TextView = view.findViewById(R.id.goal_delete_btn)
        val toggleBtn: TextView = view.findViewById(R.id.goal_toggle_btn)
        val editIcon: ImageView = view.findViewById(R.id.goal_edit_icon)
        val buttonLayout: View = view.findViewById(R.id.goal_button_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_list_recyclerview, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.description
        setupDonutChart(holder.pieChart, item.percent)

        holder.buttonLayout.visibility = if (isEditMode) View.VISIBLE else View.GONE
        // holder.editIcon.visibility = if (isEditMode) View.VISIBLE else View.GONE

        holder.toggleBtn.text = if (item.isActive) "비활성화하기" else "활성화하기"


        if (item.isActive) {
            // 비활성화 상태
            holder.toggleBtn.setTextColor(holder.toggleBtn.context.getColor(R.color.blue_300))
            holder.toggleBtn.setBackgroundResource(R.drawable.bg_toggle_button)
        } else {
            // 활성화 상태
            holder.toggleBtn.setTextColor(holder.toggleBtn.context.getColor(R.color.green_300)) // 진한 초록
            holder.toggleBtn.setBackgroundResource(R.drawable.bg_activate_button) // 연한 초록 배경
        }

        holder.toggleBtn.setOnClickListener {
            if (item.isActive) {
                // 현재 활성 상태이므로 → 비활성화 다이얼로그 표시
                showInactivateDialog(holder.toggleBtn, item, position)
            } else {
                // 현재 비활성 상태이므로 → 활성화 다이얼로그 표시
                showActivateDialog(holder.toggleBtn, item, position)
            }
        }

        holder.deleteBtn.setOnClickListener {
            showDeleteDialog(holder.toggleBtn, item, position)
        }

        holder.itemView.setOnClickListener {
            if (!isEditMode) {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun toggleEditMode(): Boolean {
        isEditMode = !isEditMode
        notifyDataSetChanged()
        return isEditMode
    }

    private fun setupDonutChart(pieChart: PieChart, percent: Int) {
        val entries = listOf(
            PieEntry(percent.toFloat()),
            PieEntry((100 - percent).toFloat())
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.rgb(51, 102, 255),
                Color.rgb(230, 230, 230)
            )
            setDrawValues(false)
            sliceSpace = 2f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(false)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 75f
            transparentCircleRadius = 0f
            centerText = "$percent%"
            setCenterTextSize(12f)
            setCenterTextColor(Color.BLACK)
            invalidate()
        }
    }

    private fun showInactivateDialog(anchorView: View, item: GoalItem, position: Int) {
        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_inactivate_goal)
        dialog.setCancelable(true)

        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)

        noBtn.setOnClickListener {
            dialog.dismiss() // 아무것도 하지 않음
        }

        yesBtn.setOnClickListener {
            item.isActive = !item.isActive // 상태 전환
            notifyItemChanged(position)
            onDeactivateConfirmed()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showActivateDialog(anchorView: View, item: GoalItem, position: Int) {
        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_activate_goal)
        dialog.setCancelable(true)

        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)

        noBtn.setOnClickListener {
            dialog.dismiss() // 아무것도 하지 않음
        }

        yesBtn.setOnClickListener {
            item.isActive = !item.isActive // 상태 전환
            notifyItemChanged(position)
            onActivateConfirmed()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showDeleteDialog(anchorView: View, item: GoalItem, position: Int) {
        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_delete_goal)
        dialog.setCancelable(true)

        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)

        noBtn.setOnClickListener {
            dialog.dismiss() // 아무것도 하지 않음
        }

        yesBtn.setOnClickListener {
            onDeleteConfirmed()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


}