package com.example.planup.main.friend.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.planup.component.button.PlanUpSmallButton
import com.example.planup.component.button.SmallButtonType
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.friend.ui.common.FriendProfileRow
import com.example.planup.main.friend.ui.sheet.FriendReportSheet
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.network.dto.friend.FriendInfo

class FriendListsFragment : Fragment() {
    private var _binding: FragmentFriendListsBinding? = null
    val binding: FragmentFriendListsBinding
        get() = _binding!!

    private val friendViewModel: FriendViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.friendListsRecyclerView.setContent {
            val friendList by friendViewModel.friendList.collectAsState()
            FriendListView(
                friendList = friendList,
                reportFriend = { friend, reason, withBlock ->

                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //    /** 친구 목록 조회 */
//    private fun fetchFriends() {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader()
//            if (auth.isNullOrBlank()) {
//                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
//                return@launch
//            }
//
//            try {
//                val resp = RetrofitInstance.friendApi.getFriendSummary(auth)
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    val list: List<FriendInfo> =
//                        resp.body()!!.result.firstOrNull()?.friendInfoSummaryList.orEmpty()
//
//                    binding.friendListsRecyclerView.adapter = FriendListsAdapter(
//                        items = list,
//                        onDeleteClick = { friend -> deleteDialog(friend) },
//                        onBlockClick = { friend -> blockDialog(friend) },
//                        onReportClick = { friend -> showReportDialog(friend) }
//                    )
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        resp.body()?.message ?: "목록을 불러오지 못했습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    /** “삭제” 동작
//     * 서버에 ‘삭제’ API가 별도로 없다면, 실사용에선 ‘차단’으로 대체하거나
//     * 백엔드에 “언프렌드” 엔드포인트를 요청해야 합니다.
//     * 여기서는 편의상 ‘차단’과 동일하게 처리합니다.
//     */
//    /** 삭제 API **/
//    private fun deleteFriend(friend: FriendInfo) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val resp = RetrofitInstance.friendApi.deleteFriend(
//                    token = auth,
//                    request = friend.id
//                )
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(
//                        requireContext(),
//                        "${friend.nickname} 님을 삭제했어요.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    fetchFriends() // 갱신
//                } else {
//                    Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    /** 차단 API */
//    private fun blockFriend(friend: FriendInfo) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val resp = RetrofitInstance.friendApi.blockFriend(
//                    token = auth,
//                    request = friend.id
//                )
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(
//                        requireContext(),
//                        "${friend.nickname} 님을 차단했어요.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    fetchFriends() // 갱신
//                } else {
//                    Toast.makeText(requireContext(), "차단에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    /** 신고 API (간단히 고정 사유로 전송) */
//    private fun reportFriend(friend: FriendInfo) {
//        lifecycleScope.launch {
//            val auth = buildAuthHeader() ?: return@launch
//            try {
//                val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
//                val userId = prefs.getInt("userId", -1)
//                if (userId <= 0) {
//                    Toast.makeText(requireContext(), "유효한 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
//                    return@launch
//                }
//
//                val resp = RetrofitInstance.friendApi.reportFriend(
//                    request = FriendReportRequestDto(
//                        userId = userId,
//                        friendId = friend.id,
//                        reason = "부적절한 행동 신고", // TODO: 신고 사유 입력 UI로 대체 가능
//                        block = false               // 필요 시 true로 신고+차단 동시 처리
//                    )
//                )
//                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                    Toast.makeText(requireContext(), "신고가 접수되었어요.", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "신고에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    /** 신고 다이얼로그 띄우기 */
//    private fun showReportDialog(friend: FriendInfo) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        dialog.setContentView(R.layout.dialog_report_friend)
//        dialog.window?.apply {
//            setLayout(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//            setGravity(Gravity.BOTTOM)
//            setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))
//        }
//
//        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radio_group_reasons)
//        val switchBlock = dialog.findViewById<Switch>(R.id.switch_block_user)
//        val btnSubmit = dialog.findViewById<Button>(R.id.btn_report_submit)
//
//        btnSubmit.setOnClickListener {
//            // 선택된 라디오버튼 텍스트 추출
//            val checkedId = radioGroup.checkedRadioButtonId
//            if (checkedId == View.NO_ID) {
//                Toast.makeText(requireContext(), "신고 사유를 선택해 주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val reason =
//                dialog.findViewById<RadioButton>(checkedId)?.text?.toString()?.trim().orEmpty()
//            val block = switchBlock.isChecked
//
//            sendReport(friend, reason, block) {
//                dialog.dismiss()
//            }
//        }
//
//        dialog.show()
//    }
//
//    /** 신고 전송 */
//    private fun sendReport(friend: FriendInfo, reason: String, block: Boolean, onDone: () -> Unit) {
//
//    }
//
//    private fun blockDialog(friend: FriendInfo) {
//        val dialog = Dialog(context as MainActivity)
//        dialog.setContentView(R.layout.dialog_ban_friend)
//        dialog.window?.apply {
//            setGravity(Gravity.CENTER)
//            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))
//            dialog.setCanceledOnTouchOutside(true)
//        }
//        dialog.findViewById<TextView>(R.id.popup_block_title_tv).text =
//            getString(R.string.popup_block_friend_title, friend.nickname)
//        dialog.findViewById<TextView>(R.id.popup_friend_no_btn).setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.findViewById<TextView>(R.id.popup_friend_yes_btn).setOnClickListener {
//            lifecycleScope.launch {
//                val auth = buildAuthHeader() ?: return@launch
//                try {
//                    val resp = RetrofitInstance.friendApi.blockFriend(
//                        token = auth,
//                        request = friend.id
//                    )
//                    if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                        fetchFriends() // 갱신
//
//                        val inflater = LayoutInflater.from(context)
//                        val layout = inflater.inflate(R.layout.toast_grey_template, null)
//                        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text =
//                            getString(R.string.toast_block, friend.nickname)
//
//                        val toast = Toast(context)
//                        toast.view = layout
//                        toast.duration = LENGTH_SHORT
//                        toast.setGravity(Gravity.BOTTOM, 0, 300)
//                        toast.show()
//                    } else {
//                        Toast.makeText(requireContext(), "차단에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//                }
//            }
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//    private fun deleteDialog(friend: FriendInfo) {
//        val dialog = Dialog(context as MainActivity)
//        dialog.setContentView(R.layout.dialog_delete_friend)
//        dialog.window?.apply {
//            setGravity(Gravity.CENTER)
//            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.transparent))
//            dialog.setCanceledOnTouchOutside(true)
//        }
//        dialog.findViewById<TextView>(R.id.popup_delete_title_tv).text =
//            getString(R.string.popup_delete_friend, friend.nickname)
//        dialog.findViewById<TextView>(R.id.popup_friend_no_btn).setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.findViewById<TextView>(R.id.popup_friend_yes_btn).setOnClickListener {
//            lifecycleScope.launch {
//                val auth = buildAuthHeader() ?: return@launch
//                try {
//                    val resp = RetrofitInstance.friendApi.deleteFriend(
//                        token = auth,
//                        request = friend.id
//                    )
//                    if (resp.isSuccessful && resp.body()?.isSuccess == true) {
//                        fetchFriends() // 갱신
//
//                        val inflater = LayoutInflater.from(context)
//                        val layout = inflater.inflate(R.layout.toast_grey_template, null)
//                        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text =
//                            getString(R.string.toast_delete, friend.nickname)
//
//                        val toast = Toast(context)
//                        toast.view = layout
//                        toast.duration = LENGTH_SHORT
//                        toast.setGravity(Gravity.BOTTOM, 0, 300)
//                        toast.show()
//                    } else {
//                        Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
//                }
//            }
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
}


@Composable
private fun FriendListView(
    friendList: List<FriendInfo>,
    reportFriend: (friend: FriendInfo, reason: String, withBlock: Boolean) -> Unit
) {
    LazyColumn(

    ) {
        items(friendList) { friend ->
            FriendListItem(
                friend = friend,
                reportFriend = { reason, withBlock ->
                    reportFriend(friend, reason, withBlock)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendListItem(
    friend: FriendInfo,
    reportFriend: (reason: String, withBlock: Boolean) -> Unit
) {
    var showReportSheet by remember {
        mutableStateOf(false)
    }
    FriendProfileRow(
        profileImage = friend.profileImage,
        friendName = friend.nickname
    ) {
        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Red,
            onClick = {

            },
            title = "삭제"
        )
        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Green,
            onClick = {

            },
            title = "차단"
        )

        PlanUpSmallButton(
            smallButtonType = SmallButtonType.Blue,
            onClick = {
                showReportSheet = true
            },
            title = "신고"
        )
    }
    if (showReportSheet) {
        FriendReportSheet(
            showWithBlock = true,
            onDismissRequest = {
                showReportSheet = false
            },
            reportFriend = { reason, withBlock ->
                reportFriend(reason, withBlock)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendListItemPreview() {
    FriendListItem(
        friend = FriendInfo(
            1,
            "test",
            1,
            "",
            true,
            ""
        ),
        reportFriend = { _, _ -> }
    )
}