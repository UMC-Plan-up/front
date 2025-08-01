package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetPhotoBinding
import com.example.planup.goal.GoalActivity

class ChallengeSetPhotoFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetPhotoBinding

    private lateinit var selectBackground: View //선택된 요일의 프레임
    private lateinit var selectText: TextView //선택된 요일의 텍스트
    private var isFirst: Boolean = true //종료일을 아직 선택하지 않은 경우

    private lateinit var popupWindow: PopupWindow //기준일 설정 드롭다운
    private var finish: Boolean = false //종료일 설정 여부
    private var duration: Boolean = false //기준 기간 설정 여부
    private var often: Boolean = false //빈도 설정 여부
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetPhotoBinding.inflate(inflater, container, false)
        clickListener()
        textListener()
        return binding.root
    }

    private fun clickListener() {

        //뒤로가기, 인증방식 설정 페이지로 이동
        binding.photoBackIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, ChallengeTimerPhotoFragment())
                .commitAllowingStateLoss()
        }
        /* 요일 선택 효과 */
        // 월요일
        binding.photoMondayCl.setOnClickListener {
            if (binding.photoMondayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoMondayCl, binding.photoMondayTv)
        }

        // 화요일
        binding.photoTuesdayCl.setOnClickListener {
            if (binding.photoTuesdayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoTuesdayCl, binding.photoTuesdayTv)
        }

        // 수요일
        binding.photoWednesdayCl.setOnClickListener {
            if (binding.photoWednesdayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoWednesdayCl, binding.photoWednesdayTv)
        }

        // 목요일
        binding.photoThursdayCl.setOnClickListener {
            if (binding.photoThursdayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoThursdayCl, binding.photoThursdayTv)
        }

        // 금요일
        binding.photoFridayCl.setOnClickListener {
            if (binding.photoFridayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoFridayCl, binding.photoFridayTv)
        }

        // 토요일
        binding.photoSaturdayCl.setOnClickListener {
            if (binding.photoSaturdayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoSaturdayCl, binding.photoSaturdayTv)
        }

        // 일요일
        binding.photoSundayCl.setOnClickListener {
            if (binding.photoSundayCl.isSelected) return@setOnClickListener
            setEndDay(binding.photoSundayCl, binding.photoSundayTv)
        }

        //기준 기간
        binding.photoDurationCl.setOnClickListener {
            setDuration(binding.photoDurationCl)
        }

        //다음 버튼 클릭: 페널티 설정 화면으로 이동
        binding.btnNextTv.setOnClickListener {
            if (!binding.btnNextTv.isActivated) return@setOnClickListener
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container, ChallengePenaltyFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun setEndDay(background: View, text: TextView) {
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)

        //이미 다른 요일을 선택한 경우
        if (!isFirst) { //이전 선택 요일 해제
            selectBackground.isSelected = !selectBackground.isSelected
            selectText.setTextColor(unselected)
        }
        isFirst = false

        //현재 선택 요일 변경
        selectBackground = background
        selectText = text
        //선택 요일 표시
        selectBackground.isSelected = !selectBackground.isSelected
        selectText.setTextColor(selected)
        finish = true

        binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
    }

    private fun textListener() {
        //빈도
        binding.photoOftenEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.photoOftenEt.text.isNotEmpty()
                    && binding.photoOftenEt.text.toString().toInt() >= 1
                ) {
                    binding.photoErrorTv.visibility = View.GONE
                    binding.photoErrorIv.visibility = View.GONE
                    often = true
                } else {
                    binding.photoErrorTv.visibility = View.VISIBLE
                    binding.photoErrorIv.visibility = View.VISIBLE
                    often = false
                }
                binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun setDuration(view: View) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dropdown_photo_duration, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(view)
        //popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.isOutsideTouchable = true
        popupView.findViewById<View>(R.id.duration_one_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.one)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_two_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.two)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_three_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.three)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_four_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.four)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_five_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.five)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_six_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.six)
            popupWindow.dismiss()
            duration = true
        }
        popupView.findViewById<View>(R.id.duration_seven_tv).setOnClickListener {
            binding.photoDurationTv.text = getString(R.string.seven)
            popupWindow.dismiss()
            duration = true
        }

        binding.btnNextTv.isActivated = finish && duration && often //다음 버튼 활성화 여부 확인
    }
}