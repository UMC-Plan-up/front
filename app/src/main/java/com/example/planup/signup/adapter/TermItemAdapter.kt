package com.example.planup.signup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.network.dto.term.TermItem

class TermItemAdapter(
    private val terms: List<TermItem>,
    private val onCheckedChanged: () -> Unit,
    private val onDetailClicked: (termsId: Int) -> Unit
) : RecyclerView.Adapter<TermItemAdapter.TermViewHolder>() {

    private val checkedMap = mutableMapOf<Int, Boolean>()

    inner class TermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.termCheckBox)
        val summary: TextView = itemView.findViewById(R.id.termSummary)
        val detail: TextView = itemView.findViewById(R.id.termDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_term, parent, false)
        return TermViewHolder(view)
    }

    override fun getItemCount(): Int = terms.size

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val term = terms[position]
        holder.summary.text = term.summary

        holder.checkBox.buttonTintList = holder.checkBox.context.getColorStateList(R.color.black_400)
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = checkedMap[term.id] ?: false
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkedMap[term.id] = isChecked
            onCheckedChanged()
        }

        holder.detail.visibility = if (term.isRequired) View.VISIBLE else View.GONE
        holder.detail.setOnClickListener {
            onDetailClicked(term.id)
        }
    }

    // 전체동의 동작 시 호출
    fun setAllChecked(isChecked: Boolean) {
        terms.forEach { checkedMap[it.id] = isChecked }
        notifyDataSetChanged()
    }

    // 현재 체크된 약관 반환
    fun getCheckedTerms(): List<TermItem> {
        return terms.filter { checkedMap[it.id] == true }
    }
}
