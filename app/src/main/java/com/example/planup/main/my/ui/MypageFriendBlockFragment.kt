package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageFriendBlockBinding
import com.example.planup.main.my.adapter.BlockFriendRVAdapter
import com.example.planup.main.my.data.BlockedFriend
import com.example.planup.network.adapter.FriendReportAdapter
import com.example.planup.network.adapter.FriendsBlockedAdapter
import com.example.planup.network.adapter.FriendsUnblockedAdapter
import com.example.planup.network.controller.FriendController
import com.example.planup.network.data.BlockedFriends
import com.example.planup.network.dto.friend.FriendReportDto
import com.example.planup.network.dto.friend.FriendUnblockDto

class MypageFriendBlockFragment : Fragment(), FriendsBlockedAdapter, FriendReportAdapter,
    FriendsUnblockedAdapter {
    lateinit var binding: FragmentMypageFriendBlockBinding
    private val friends = ArrayList<BlockedFriend>()
    private lateinit var rvAdapter: BlockFriendRVAdapter
    private lateinit var controller: FriendController
    private lateinit var prefs: SharedPreferences

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
    private fun init() {
        //API 컨트롤러 생성
        controller = FriendController()
        //차단 친구 목록 요청
        controller.friendsBlockedService()
        //차단 친구 목록 출력
        controller.setFriendsBlockedAdapter(this)
        //차단 친구 신고 UI 관리
        controller.setFriendReportAdapter(this)
        //차단친구 차단 해제 UI 관리
        controller.setFriendUnblockedAdapter(this)
        //사용자 정보 호출
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
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
    private fun dialogUnblock(position: Int, friend: BlockedFriend) {
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_unblock)
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.findViewById<TextView>(R.id.popup_unblock_title_tv).text = getString(R.string.popup_unblock_title,friend.name)
        }
        //차단 유지
        dialog.findViewById<View>(R.id.popup_unblock_no_btn).setOnClickListener {
            dialog.dismiss()
        }
        //클릭한 친구 서버에 차단 해제 알리고
        //차단 친구 목록에서 해당 친구 제외
        dialog.findViewById<View>(R.id.popup_unblock_yes_btn).setOnClickListener {
            controller.friendsUnblockService(FriendUnblockDto(prefs.getInt("userId",0), friend.name))
            rvAdapter.notifyItemRemoved(position)
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    //신고 사유를 입력하는 팝업
    private fun dialogReport(friendId: Int) {
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
            controller.reportFriendService(
                FriendReportDto(
                    prefs.getInt("userId", 0),
                    friendId,
                    //신고 사유
                    dialog.findViewById<TextView>(selected).text.toString(),
                    //토글 on/off 여부로 차단 여부를 전달함
                    dialog.findViewById<View>(R.id.popup_report_block_on_iv).isVisible
                )
            )
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
                    dialogReport(friends[position].id)
                }
            }
        })

    }

    //차단 친구 목룍 불러오기 완료
    override fun successBlockedFriends(blockedFriendsList: List<BlockedFriends>?) {
        blockedFriendsList?.let { list ->
            for (friend in list) {
                friends.add(BlockedFriend(friend.friendId, friend.friendNickname, 0))
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
        Log.d("okhttp", "code: $code\nmessage: $message")
    }
}