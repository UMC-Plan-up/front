package com.example.planup.main.my.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageFriendBlockBinding
import com.example.planup.main.my.adapter.BlockFriendRVAdapter
import com.example.planup.main.my.data.BlockFriend

class MypageFriendBlockFragment:Fragment() {
    lateinit var binding: FragmentMypageFriendBlockBinding
    private val friends = ArrayList<BlockFriend>()
    lateinit var rvAdapter: BlockFriendRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageFriendBlockBinding.inflate(inflater,container,false)
        clickListener()
        showBlockFriend()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.emailSecondBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }


    }
    private fun showBlockFriend(){
        friends.add(BlockFriend("친구1", R.drawable.ic_profile))
        friends.add(BlockFriend("친구2", R.drawable.ic_profile))
        friends.add(BlockFriend("친구3", R.drawable.ic_profile))
        friends.add(BlockFriend("친구4", R.drawable.ic_profile))
        friends.add(BlockFriend("친구5", R.drawable.ic_profile))
        friends.add(BlockFriend("친구6", R.drawable.ic_profile))
        friends.add(BlockFriend("친구7", R.drawable.ic_profile))

        rvAdapter = BlockFriendRVAdapter(friends)
        binding.friendBlockListRv.adapter = rvAdapter

        rvAdapter.setFriendHandler(object : BlockFriendRVAdapter.FriendHandler{
            override fun manageFriend(position: Int, action: Int) {
                if (action == 0){
                    dialogUnblock(position, friends[position])
                } else{
                    dialogReport(position)
                }
            }
        })

    }
    private fun dialogUnblock(position: Int, friend:BlockFriend){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_unblock)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.findViewById<View>(R.id.popup_unblock_no_btn).setOnClickListener{
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.popup_unblock_yes_btn).setOnClickListener{
            rvAdapter.notifyItemRemoved(position)
            dialog.dismiss()
            makeToast(getString(R.string.toast_unblock,friend.name))
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
    private fun dialogReport(position: Int) {
        val dialog = Dialog(context as MainActivity)
        var selected = R.id.popup_report_swear_tv
        dialog.setContentView(R.layout.popup_report)
        dialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // 신고 사유 선택 효과
        dialog.findViewById<View>(R.id.popup_report_swear_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_swear_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_sexual_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_sexual_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_spam_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_spam_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_fraud_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_fraud_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_personal_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_personal_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_improper_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_improper_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_other_tv).setOnClickListener{
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_other_tv
            dialog.findViewById<View>(selected).isSelected = true
        }

        //차단하기 토글
        dialog.findViewById<View>(R.id.popup_report_block_on_iv).setOnClickListener {
            dialog.findViewById<View>(R.id.popup_report_block_on_iv).visibility = View.GONE
            dialog.findViewById<View>(R.id.popup_report_block_off_iv).visibility = View.VISIBLE
        }
        dialog.findViewById<View>(R.id.popup_report_block_off_iv).setOnClickListener {
            dialog.findViewById<View>(R.id.popup_report_block_on_iv).visibility = View.VISIBLE
            dialog.findViewById<View>(R.id.popup_report_block_off_iv).visibility = View.GONE
        }

        //신고 완료 버튼
        dialog.findViewById<View>(R.id.popup_report_complete_btn).setOnClickListener {
            dialog.dismiss()
            makeToast(getString(R.string.toast_report))
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun makeToast(message: String){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_blue_template,null)
        layout.findViewById<TextView>(R.id.toast_blue_template_tv).text = message

        val toast = Toast(context)
        toast.duration = LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }
}