package com.planup.planup.main.goal.adapter

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.databinding.ItemGoalListRecyclerviewBinding
import com.planup.planup.main.goal.item.GoalItem
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GoalAdapter(
    private var items: List<GoalItem>,
    private val onItemClick: (GoalItem) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val onDeactivateConfirmed: (Int) -> Unit,
    private val onActivateConfirmed: (Int) -> Unit,
    private val onDeleteConfirmed: (Int,()->Unit) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var isEditMode: Boolean = false

    interface dialogShower{
        fun showDialog()
    }

    class GoalViewHolder(val binding: ItemGoalListRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val pieChart: PieChart = binding.goalListRvPc
        val title: TextView = binding.goalListTitleTv
        val description: TextView = binding.goalListDescriptionTv
        val deleteBtn: TextView = binding.goalDeleteBtn
        val toggleBtn: TextView = binding.goalToggleBtn
        val editIcon: ImageView = binding.goalEditIconIv
        val buttonLayout: View = binding.goalButtonContainer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalListRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalViewHolder(binding)
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
            //삭제 다이얼로그 표시
            showDeleteDialog(holder.toggleBtn, item, position)
        }

        holder.itemView.setOnClickListener {
            if (!isEditMode) {
                onItemClick(item)
            }
        }

        holder.editIcon.setOnClickListener {
//            val goalId = item.goalId
//            val context = holder.itemView.context
//            val intent = Intent(context, EditFriendGoalActivity::class.java)
//            intent.putExtra("goalId", goalId)  // 필요한 정보 전달
//            context.startActivity(intent)
            onEditClick(item.goalId)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GoalItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun setEditMode(enabled: Boolean) {
        this.isEditMode = enabled
        notifyDataSetChanged()
    }

    fun removeItemById(goalId: Int){
        val idx = items.indexOfFirst{it.goalId == goalId}
        if(idx != -1){
            items = items.toMutableList().also{it.removeAt(idx)}
            notifyItemRemoved(idx)
        }
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
//        val dialog = Dialog(anchorView.context)
//        dialog.setContentView(R.layout.dialog_inactivate_goal)
//        dialog.setCancelable(true)
//
//        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
//        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)
//
//        noBtn.setOnClickListener {
//            dialog.dismiss() // 아무것도 하지 않음
//        }
//
//        yesBtn.setOnClickListener {
//            item.isActive = !item.isActive // 상태 전환
////            notifyItemChanged(position)
//            // onDeactivateConfirmed(item.goalId)
//            dialog.dismiss()
//        }
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_inactivate_goal)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.findViewById<TextView>(R.id.popup_inactivate_goal_no_btn).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.popup_inactivate_goal_yes_btn).setOnClickListener{

//            item.isActive = !item.isActive // 상태 전환
            onDeactivateConfirmed(item.goalId)
//            notifyItemChanged(position)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showActivateDialog(anchorView: View, item: GoalItem, position: Int) {
//        val dialog = Dialog(anchorView.context)
//        dialog.setContentView(R.layout.dialog_activate_goal)
//        dialog.setCancelable(true)
//
//        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
//        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)
//
//        noBtn.setOnClickListener {
//            dialog.dismiss() // 아무것도 하지 않음
//        }
//
//        yesBtn.setOnClickListener {
//            item.isActive = !item.isActive // 상태 전환
//            notifyItemChanged(position)
//            // onActivateConfirmed(item.goalId)
//            dialog.dismiss()
//        }
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()

        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_activate_goal)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.findViewById<TextView>(R.id.popup_activate_goal_no_btn).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.popup_activate_goal_yes_btn).setOnClickListener{
//            item.isActive = !item.isActive // 상태 전환
            onActivateConfirmed(item.goalId)
//            notifyItemChanged(position)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeleteDialog(anchorView: View, item: GoalItem, position: Int) {
//        val dialog = Dialog(anchorView.context)
//        dialog.setContentView(R.layout.dialog_delete_goal)
//        dialog.setCancelable(true)
//
//        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
//        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)
//
//        noBtn.setOnClickListener {
//            dialog.dismiss() // 아무것도 하지 않음
//        }
//
//        yesBtn.setOnClickListener {
//            onDeleteConfirmed(item.goalId)
//            dialog.dismiss()
//        }
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
        val dialog = Dialog(anchorView.context)
        dialog.setContentView(R.layout.dialog_delete_goal)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.findViewById<TextView>(R.id.popup_delete_goal_no_btn).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.popup_delete_goal_yes_btn).setOnClickListener{
//            item.isActive = !item.isActive // 상태 전환
            onDeleteConfirmed(item.goalId){
                notifyItemChanged(position)
                dialog.dismiss()
            }

        }
        dialog.show()
    }

    fun updateItemActive(goalId: Int, active: Boolean){
        val idx = items.indexOfFirst{it.goalId==goalId}
        if(idx!=-1){
            val m = items.toMutableList()
            m[idx] = m[idx].copy(isActive = active)
            items = m
            notifyItemChanged(idx)
        }
    }


}