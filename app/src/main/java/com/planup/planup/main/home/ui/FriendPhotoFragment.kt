package com.planup.planup.main.home.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.planup.planup.R
import com.planup.planup.databinding.FragmentFriendPhotoBinding
import com.planup.planup.main.home.adapter.PhotoAdapter
import com.planup.planup.main.home.ui.viewmodel.FriendPhotoViewModel
import com.planup.planup.network.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendPhotoFragment : Fragment() {
    private var _binding: FragmentFriendPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendPhotoViewModel by viewModels()

    companion object {
        const val FRIEND_ID = "friendId"
        const val GOAL_ID = "goalId"

        fun newInstance(friendId: Int, goalId: Int): FriendPhotoFragment {
            return FriendPhotoFragment().apply {
                arguments = Bundle().apply {
                    putInt(FRIEND_ID, friendId)
                    putInt(GOAL_ID, goalId)
                }
            }
        }
    }

    // 1. 레이아웃을 인플레이트하는 과정이 필요합니다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.friendPhotoBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.friendPhotoMoreIv.setOnClickListener {
            showMorePopup(binding.friendPhotoMoreIv)
        }

        loadPhotos()
    }

    private fun loadPhotos() {
        val photoRv = binding.friendPhotoRv
        val adapter = PhotoAdapter()
        photoRv.adapter = adapter
        viewModel.loadFriendPhoto(createErrorHandler("loadFriendPhotos") { result ->
            val urls = result.map { it.photoImg }
            adapter.submitList(urls)
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photoList.collectLatest { list ->
                    val urls = list.map { it.photoImg }
                    adapter.submitList(urls)
                }
            }
        }

    }

    private fun showMorePopup(anchorView: View) {
        val inflater = LayoutInflater.from(requireActivity())
        val popupView = inflater.inflate(R.layout.popup_comment_more, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupView.findViewById<LinearLayout>(R.id.layout_block).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.layout_report).setOnClickListener {
            popupWindow.dismiss()
            showReportPopup(anchorView)
        }

        popupWindow.showAsDropDown(anchorView, 0, 12)
    }
    private fun showReportPopup(anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_comment_report, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        fun setClick(id: Int, reason: String) {
            popupView.findViewById<View>(id).setOnClickListener {
                viewModel.postReport(reason, true)
                popupWindow.dismiss()
            }
        }
        setClick(R.id.layout_abuse, "ABUSE_OR_HATE_SPEECH")
        setClick(R.id.layout_sexual, "SEXUAL_CONTENT")
        setClick(R.id.layout_spam, "SPAM_OR_ADVERTISING")
        setClick(R.id.layout_inappropriate, "INAPPROPRIATE_CONTENT")
        setClick(R.id.layout_fake, "FRAUD_OR_IMPERSONATION")
        setClick(R.id.layout_other, "OTHER")

        popupWindow.showAsDropDown(anchorView,0,12)
    }

    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}