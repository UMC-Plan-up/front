package com.example.planup.main.friend.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planup.R
import com.example.planup.databinding.FragmentFriendListsBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.friend.adapter.FriendListsAdapter
import com.example.planup.main.friend.data.FriendActionRequestDto
import com.example.planup.main.friend.data.FriendInfo
import com.example.planup.main.friend.data.FriendReportRequestDto
import com.example.planup.network.RetrofitInstance
import kotlinx.coroutines.launch

class FriendListsFragment : Fragment() {
    lateinit var binding: FragmentFriendListsBinding
    private var friends: List<FriendInfo> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendListsBinding.inflate(inflater, container, false)
        binding.friendListsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendListsRecyclerView.adapter = FriendListsAdapter(
            items = friends,
            onDeleteClick = { friend -> deleteFriend(friend) },
            onBlockClick  = { friend -> blockFriend(friend) },
            onReportClick = { friend -> showReportDialog(friend) } // ← 여기!
        )
        clickListener()
        fetchFriends()
        return binding.root
    }

    /** 로그인 토큰 구성: userInfo prefs 우선, 없으면 App.jwt.token 폴백 */
    private fun buildAuthHeader(): String? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val prefsToken = prefs.getString("accessToken", null)
        val appToken = com.example.planup.network.App.jwt.token

        val raw = when {
            !prefsToken.isNullOrBlank() -> prefsToken
            !appToken.isNullOrBlank() -> appToken
            else -> null
        } ?: return null

        return if (raw.startsWith("Bearer ", ignoreCase = true)) raw else "Bearer $raw"
    }

    /** 친구 목록 조회 */
    private fun fetchFriends() {
        lifecycleScope.launch {
            val auth = buildAuthHeader()
            if (auth.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val resp = RetrofitInstance.friendApi.getFriendSummary(auth)
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    val list: List<FriendInfo> =
                        resp.body()!!.result.firstOrNull()?.friendInfoSummaryList.orEmpty()

                    binding.friendListsRecyclerView.adapter = FriendListsAdapter(
                        items = list,
                        onDeleteClick = { friend -> deleteFriend(friend) },
                        onBlockClick = { friend -> blockFriend(friend) },
                        onReportClick = { friend -> reportFriend(friend) }
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        resp.body()?.message ?: "목록을 불러오지 못했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "서버 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** “삭제” 동작
     * 서버에 ‘삭제’ API가 별도로 없다면, 실사용에선 ‘차단’으로 대체하거나
     * 백엔드에 “언프렌드” 엔드포인트를 요청해야 합니다.
     * 여기서는 편의상 ‘차단’과 동일하게 처리합니다.
     */
    private fun deleteFriend(friend: FriendInfo) {
        blockFriend(friend) // 임시로 차단과 동일 처리
    }

    /** 차단 API */
    private fun blockFriend(friend: FriendInfo) {
        lifecycleScope.launch {
            val auth = buildAuthHeader() ?: return@launch
            try {
                val resp = RetrofitInstance.friendApi.blockFriend(
                    token = auth,
                    request = FriendActionRequestDto(friendId = friend.id)
                )
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "${friend.nickname} 님을 차단했어요.", Toast.LENGTH_SHORT).show()
                    fetchFriends() // 갱신
                } else {
                    Toast.makeText(requireContext(), "차단에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** 신고 API (간단히 고정 사유로 전송) */
    private fun reportFriend(friend: FriendInfo) {
        lifecycleScope.launch {
            val auth = buildAuthHeader() ?: return@launch
            try {
                val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                val userId = prefs.getInt("userId", -1)
                if (userId <= 0) {
                    Toast.makeText(requireContext(), "유효한 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val resp = RetrofitInstance.friendApi.reportFriend(
                    token = auth,
                    request = FriendReportRequestDto(
                        userId = userId,
                        friendId = friend.id,
                        reason = "부적절한 행동 신고", // TODO: 신고 사유 입력 UI로 대체 가능
                        block = false               // 필요 시 true로 신고+차단 동시 처리
                    )
                )
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "신고가 접수되었어요.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "신고에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** userId 로드 */
    private fun getUserIdOrNull(): Int? {
        val prefs = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val id = prefs.getInt("userId", -1)
        return if (id > 0) id else null
    }

    /** 신고 다이얼로그 띄우기 */
    private fun showReportDialog(friend: FriendInfo) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_report_friend)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radio_group_reasons)
        val switchBlock = dialog.findViewById<Switch>(R.id.switch_block_user)
        val btnSubmit = dialog.findViewById<Button>(R.id.btn_report_submit)

        btnSubmit.setOnClickListener {
            // 선택된 라디오버튼 텍스트 추출
            val checkedId = radioGroup.checkedRadioButtonId
            if (checkedId == View.NO_ID) {
                Toast.makeText(requireContext(), "신고 사유를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val reason = dialog.findViewById<RadioButton>(checkedId)?.text?.toString()?.trim().orEmpty()
            val block = switchBlock.isChecked

            sendReport(friend, reason, block) {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    /** 신고 전송 */
    private fun sendReport(friend: FriendInfo, reason: String, block: Boolean, onDone: () -> Unit) {
        lifecycleScope.launch {
            val auth = buildAuthHeader()
            if (auth.isNullOrBlank()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val userId = getUserIdOrNull()
            if (userId == null) {
                Toast.makeText(requireContext(), "유효한 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val resp = RetrofitInstance.friendApi.reportFriend(
                    token = auth,
                    request = FriendReportRequestDto(
                        userId = userId,
                        friendId = friend.id,
                        reason = reason,
                        block = block
                    )
                )

                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show()
                    onDone()
                    // 필요 시 목록 갱신
                    fetchFriends()
                } else {
                    Toast.makeText(
                        requireContext(),
                        resp.body()?.message ?: "신고에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun clickListener() {
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }
    }
}