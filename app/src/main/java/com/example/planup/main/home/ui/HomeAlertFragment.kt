package com.example.planup.main.home.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentHomeAlertBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.adapter.AlertVPAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeAlertFragment : Fragment() {
    lateinit var binding: FragmentHomeAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAlertBinding.inflate(inflater, container, false)
        clickListener()
        setTabLayout() //탭 레이아웃

        return binding.root
    }

    private fun clickListener(){
        binding.homeAlertBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
    }
    /*탭 레이아웃 설정*/
    private fun setTabLayout(){
        val category: Array<String> = resources.getStringArray(R.array.challenge) //탭 메뉴 텍스트

        val adapter = AlertVPAdapter(this) //탭 레이아웃 <-> 뷰 페이저 연결
        binding.friendAlertVp.adapter = adapter //탭 레이아웃에 어댑터 설정
        TabLayoutMediator(binding.friendAlertTl, binding.friendAlertVp) { tab, position ->
            tab.text = category[position]  //탭 메뉴에 텍스트 할당
        }.attach()

        setTabMargin(binding.friendAlertTl, 8) //탭 메뉴 간의 간격 8dp
    }

    /*탭 레이아웃의 버튼 간격 설정*/
    private fun setTabMargin(tabLayout: TabLayout, marginInDp: Int) {
        for (i in 0 until tabLayout.tabCount) {
            // 탭 그룹 뷰는 TabLayout의 첫 번째 자식(LinearLayout 같은 ViewGroup)
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.marginEnd = dpToPx(marginInDp, tab.context)
            tab.layoutParams = params
            tab.requestLayout()
        }
    }

    /*dp 단위를 px로 변환해줌*/
    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

}