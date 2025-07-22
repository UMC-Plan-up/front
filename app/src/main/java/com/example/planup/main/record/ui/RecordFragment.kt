package com.example.planup.main.record.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    lateinit var binding: FragmentRecordBinding
    // private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        clickListener()

        val monthList = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        val monthPopupView = layoutInflater.inflate(R.layout.popup_month_dropdown, null)
        val monthPopupWindow = PopupWindow(
            monthPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val monthListView = monthPopupView.findViewById<ListView>(R.id.listViewMonths)
        val monthAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_month, R.id.item_text, monthList)
        monthListView.adapter = monthAdapter

        binding.textSelectedMonth.setOnClickListener {
            val offsetY = resources.getDimensionPixelSize(R.dimen.dropdown_offset_y)
            monthPopupWindow.showAsDropDown(binding.textSelectedMonth, 0, offsetY)
        }

        monthListView.setOnItemClickListener { _, _, position, _ ->
            val selectedMonth = monthList[position]
            binding.textSelectedMonth.text = selectedMonth + "월"
            monthPopupWindow.dismiss()
        }

        // ▼ Year DropDown ▼ (새로 추가)
        val yearList = listOf("2025", "2024", "2023", "2022", "2021", "2020")

        val yearPopupView = layoutInflater.inflate(R.layout.popup_year_dropdown, null)
        val yearPopupWindow = PopupWindow(
            yearPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val yearListView = yearPopupView.findViewById<ListView>(R.id.listViewYears)
        val yearAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_year, R.id.item_text, yearList)
        yearListView.adapter = yearAdapter

        binding.textSelectedYear.setOnClickListener {
            val offsetY = resources.getDimensionPixelSize(R.dimen.dropdown_offset_y)
            yearPopupWindow.showAsDropDown(binding.textSelectedYear, 0, offsetY)
        }

        yearListView.setOnItemClickListener { _, _, position, _ ->
            val selectedYear = yearList[position]
            binding.textSelectedYear.text = selectedYear + "년"
            yearPopupWindow.dismiss()
        }

        val text1 = "그린 님을 위한\n플랜업의 "
        val text2 = "AI 응원 메시지"

        // SpannableStringBuilder를 사용해 색상 분리
        val spannable = SpannableStringBuilder()
        spannable.append(text1)
        spannable.append(text2,
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue_200)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textMessage.text = spannable



        return binding.root
    }
    private fun clickListener(){
        binding.btnBadgeRecords.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordBadgesFragment())
                .commitAllowingStateLoss()
        }

        binding.btnWeeklyReport1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordWeeklyReportFragment())
                .commitAllowingStateLoss()
        }

        binding.btnWeeklyReport2.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordWithFriendsFragment())
                .commitAllowingStateLoss()
        }

        binding.btnWeeklyReport3.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordWithFriendFragment())
                .commitAllowingStateLoss()
        }

        binding.btnWeeklyReport4.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordWithCommunityFragment())
                .commitAllowingStateLoss()
        }

        binding.btnWeeklyReport5.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordWeeklyReportFragment())
                .commitAllowingStateLoss()
        }

        binding.btnBadgeRecords.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, RecordBadgesFragment())
                .commitAllowingStateLoss()
        }

    }
}