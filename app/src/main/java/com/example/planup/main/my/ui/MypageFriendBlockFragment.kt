package com.example.planup.main.my.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageFriendBlockBinding
import com.example.planup.main.my.adapter.BlockFriendRVAdapter
import com.example.planup.main.my.data.BlockFriend
import com.example.planup.network.adapter.FriendReportAdapter
import com.example.planup.network.adapter.FriendsBlockedAdapter
import com.example.planup.network.adapter.FriendsUnblockedAdapter
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.BlockedFriend
import com.example.planup.network.entity.FriendReportDto
import com.example.planup.network.entity.FriendUnblockDto

class MypageFriendBlockFragment : Fragment(), FriendsBlockedAdapter, FriendReportAdapter, FriendsUnblockedAdapter {
    lateinit var binding: FragmentMypageFriendBlockBinding
    private val friends = ArrayList<BlockFriend>()
    private lateinit var rvAdapter: BlockFriendRVAdapter
    private lateinit var controller: UserController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageFriendBlockBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    //프레그먼트 초기화
    private fun init(){
        controller = UserController() //API 컨트롤러 생성
        controller.friendsBlockedService() //차단친구 조회 GET
        controller.setFriendsBlockedAdapter(this) //차단친구 목록 불러오는 ui
        controller.setFriendReportAdapter(this) //차단친구 신고 ui
        controller.setFriendUnblockedAdapter(this) //차단친구 차단 해제 ui
    }
    //클릭 이벤트 관리
    private fun clickListener() {
        //뒤로 가기
        binding.emailSecondBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }

    //차단 해제 여부를 묻는 팝업
    private fun dialogUnblock(position: Int, friend: BlockFriend) {
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_unblock)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        //차단 유지
        dialog.findViewById<View>(R.id.popup_unblock_no_btn).setOnClickListener {
            dialog.dismiss()
        }
        //클릭한 친구 서버에 차단 해제 알리고
        //차단 친구 목록에서 해당 친구 제외
        dialog.findViewById<View>(R.id.popup_unblock_yes_btn).setOnClickListener {
            controller.friendsUnblockService(FriendUnblockDto(123, friend.name))
            rvAdapter.notifyItemRemoved(position)
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    //신고 사유를 입력하는 팝업
    private fun dialogReport(position: Int) {
        val dialog = Dialog(context as MainActivity)
        var selected = R.id.popup_report_swear_tv
        dialog.setContentView(R.layout.popup_report)
        dialog.window?.apply {
            setGravity(Gravity.BOTTOM)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // 신고 사유 선택 효과
        dialog.findViewById<View>(R.id.popup_report_swear_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_swear_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_sexual_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_sexual_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_spam_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_spam_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_fraud_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_fraud_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_personal_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_personal_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_improper_tv).setOnClickListener {
            dialog.findViewById<View>(selected).isSelected = false
            selected = R.id.popup_report_improper_tv
            dialog.findViewById<View>(selected).isSelected = true
        }
        dialog.findViewById<View>(R.id.popup_report_other_tv).setOnClickListener {
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
            //해당 친구 신고 접수
            controller.reportFriendService(FriendReportDto(1, 2, selected.toString(), true))
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    //토스트 메시지
    private fun makeToast(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_blue_template, null)
        layout.findViewById<TextView>(R.id.toast_blue_template_tv).text = message

        val toast = Toast(context)
        toast.duration = LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    //차단 친구 목록 출력
    private fun showBlockFriend() {
        rvAdapter = BlockFriendRVAdapter(friends)
        binding.friendBlockListRv.adapter = rvAdapter

        rvAdapter.setFriendHandler(object : BlockFriendRVAdapter.FriendHandler {
            override fun manageFriend(position: Int, action: Int) {
                if (action == 0) {
                    dialogUnblock(position, friends[position])
                } else {
                    dialogReport(position)
                }
            }
        })

    }

    //차단 친구 목룍 불러오기 완료
    override fun successBlockedFriends(blockedFriendsList: List<BlockedFriend>?) {
        blockedFriendsList?.let { list ->
            for (friend in list) {
                friends.add(BlockFriend(friend.friendNickname, friend.friendId))
            }
        }
        showBlockFriend()
    }
    //차단 친구 목록 불러오기 실패
    override fun failBlockedFriends(code: String?, message: String?) {}

    //친구 신고 완료
    override fun successReportFriend() {
        makeToast(getString(R.string.toast_report))
    }
    //친구 신고 실패
    override fun failReportFriend(code: String, message: String) {
        Log.d("okhttp", "code: $code\nmessage: $message")
    }

    //친구 차단 완료
    override fun successFriendUnblock(name: String) {
        makeToast(getString(R.string.toast_unblock, name))
    }

    //친구 차단 실패
    override fun failFriendUnblock(code: String, message: String) {
        Log.d("okhttp","code: $code\nmessage: $message")
    }
}