package com.example.planup.friend

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendInviteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class FriendInviteFragment : Fragment(){
    lateinit var binding: FragmentFriendInviteBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendInviteBinding.inflate(inflater, container, false)


        clickListener()

        return binding.root
    }

    private fun clickListener(){
        binding.btnBack.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }

        binding.btnUpload.setOnClickListener {
            showPopupMenu(binding.btnUpload)
        }
    }

    /*프로필 사진 재설정 드로다운 메뉴*/
    private fun showPopupMenu(view : View) {

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.dropdown_friend_invite, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // 포커스 가능
        )

        // 팝업 바깥 클릭 시 닫힘 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable())

        // 팝업 표시 (예: 이미지뷰 아래에)
        popupWindow.showAsDropDown(view)

        popupView.findViewById<View>(R.id.album_cl).setOnClickListener{
            Toast.makeText(context,"앨범 선택",LENGTH_SHORT).show()
        }
        popupView.findViewById<View>(R.id.photo_cl).setOnClickListener{
            Toast.makeText(context,"사진 선택",LENGTH_SHORT).show()
        }
        popupView.findViewById<View>(R.id.file_cl).setOnClickListener{
            Toast.makeText(context,"파일 선택",LENGTH_SHORT).show()
        }
        popupView.findViewById<View>(R.id.file_cl).setOnClickListener {
            popupWindow.dismiss()
            showShareBottomSheet()
        }

    }

    private fun showShareBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_share_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()
    }
}