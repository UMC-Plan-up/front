package com.example.planup.friend

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendListsBinding

class FriendListsFragment : Fragment() {
    lateinit var binding: FragmentFriendListsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendListsBinding.inflate(inflater, container, false)

        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /* 뒤로 가기 버튼 */
        binding.btnBack.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }

        /* 친구 목록 삭제 버튼 */
        binding.btnDeleteFriend.setOnClickListener {
            showDeleteDialog()
        }

        /* 친구 목록 차단 버튼*/
        binding.btnBanFriend.setOnClickListener {
            showBanDialog()
        }

        /* 친구 신고 버튼 */
        binding.btnReportFriend.setOnClickListener {
            showReportDialog()
        }
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_friend)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 모서리 투명처리
        dialog.window?.setDimAmount(0.5f) // 0.0 ~ 1.0 (어두움 정도 조절)

        // 취소 버튼 클릭 시 다이얼로그 닫기
        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼 클릭 시 처리 (예: 친구 삭제 로직)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)
        yesBtn.setOnClickListener {
            // TODO: 친구 삭제 처리 로직 추가
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBanDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ban_friend)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 모서리 투명처리
        dialog.window?.setDimAmount(0.5f) // 0.0 ~ 1.0 (어두움 정도 조절)

        // 취소 버튼 클릭 시 다이얼로그 닫기
        val noBtn = dialog.findViewById<ImageView>(R.id.popup_block_no_iv)
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼 클릭 시 처리 (예: 친구 삭제 로직)
        val yesBtn = dialog.findViewById<ImageView>(R.id.popup_block_yes_iv)
        yesBtn.setOnClickListener {
            // TODO: 친구 삭제 처리 로직 추가
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showReportDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_report_friend)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 모서리 투명처리
        dialog.window?.setDimAmount(0.5f) // 0.0 ~ 1.0 (어두움 정도 조절)



        dialog.show()
    }
}