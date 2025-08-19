package com.example.planup.main.record.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentRecordBinding
import com.example.planup.main.MainActivity

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        // ▼ 드롭다운 설정
        setupDropdown(
            targetView = binding.textSelectedMonth,
            items = listOf("월", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
            popupLayoutRes = R.layout.popup_month_dropdown,
            listViewId = R.id.listViewMonths,
            itemLayoutRes = R.layout.item_dropdown_month,
            onItemSelected = { selected ->
                binding.textSelectedMonth.text = "${selected}월"
            }
        )

        setupDropdown(
            targetView = binding.textSelectedYear,
            items = listOf("연도", "2025", "2024", "2023", "2022", "2021", "2020"),
            popupLayoutRes = R.layout.popup_year_dropdown,
            listViewId = R.id.listViewYears,
            itemLayoutRes = R.layout.item_dropdown_year,
            onItemSelected = { selected ->
                binding.textSelectedYear.text = "${selected}년"
            }
        )

        spannableText()
        clickListener()

        return binding.root
    }

    // ▼ Spannable 텍스트 설정
    private fun spannableText(){
        val text1 = "그린 님을 위한\n플랜업의 "
        val text2 = "AI 응원 메시지"
        binding.textMessage.text = SpannableStringBuilder().apply {
            append(text1)
            append(
                text2,
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue_200)),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // ▼ 클릭 리스너 설정
    private fun clickListener(){
        setFragmentClick(binding.btnBadgeRecords, RecordBadgesFragment())
        setFragmentClick(binding.btnWeeklyReport1, RecordWeeklyReportFragment())
        setFragmentClick(binding.btnWeeklyReport2, RecordWithFriendsFragment())
        setFragmentClick(binding.btnWeeklyReport3, RecordWithFriendsFragment())
        setFragmentClick(binding.btnWeeklyReport4, RecordWithCommunityFragment())
        setFragmentClick(binding.btnWeeklyReport5, RecordWeeklyReportFragment())

    }

    /** 공통 드롭다운 생성 함수 */
    private fun setupDropdown(
        targetView: TextView,
        items: List<String>,
        popupLayoutRes: Int,
        listViewId: Int,
        itemLayoutRes: Int,
        onItemSelected: (String) -> Unit
    ) {
        val popupView = layoutInflater.inflate(popupLayoutRes, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val listView = popupView.findViewById<ListView>(listViewId)
        val adapter = ArrayAdapter(requireContext(), itemLayoutRes, R.id.item_text, items)
        listView.adapter = adapter

        targetView.setOnClickListener {
            val offsetY = resources.getDimensionPixelSize(R.dimen.dropdown_offset_y)
            popupWindow.showAsDropDown(targetView, 0, offsetY)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = items[position]
            onItemSelected(selected)
            popupWindow.dismiss()
        }
    }

    /** 공통 Fragment 전환 처리 */
    private fun setFragmentClick(view: View, fragment: Fragment) {
        view.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}