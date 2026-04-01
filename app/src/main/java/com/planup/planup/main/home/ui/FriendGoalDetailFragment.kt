package com.planup.planup.main.home.ui


import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.planup.planup.databinding.FragmentFriendGoalDetailBinding
import com.planup.planup.main.home.item.CustomCombinedChartRenderer
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.planup.planup.R
import com.planup.planup.main.goal.item.GetCommentsResult
import com.planup.planup.main.home.adapter.CommentAdapter
import com.planup.planup.main.home.adapter.PhotoAdapter
import com.planup.planup.main.home.ui.viewmodel.FriendGoalDetailViewModel
import com.planup.planup.network.ApiResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendGoalDetailFragment : Fragment() {

    private var _binding: FragmentFriendGoalDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var chart: CombinedChart
    private val viewModel: FriendGoalDetailViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFriendGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = viewModel.title
        binding.friendDetailTitleTv.text = title
        binding.friendDetailWeekFocusTv.text = getString(R.string.friend_detail_week_focus, title)
        binding.friendGoalDetailTodayfocusTv.text = getString(R.string.friend_goal_detail_today_text, title)
        loadComment()
        loadTodayFriendTime()
        loadFriendPhotos()
        setupCommentRv()
        pushDummyComment() //TODO: dummy comment

        chart = binding.friendGoalChart
        setupCombinedChart()

        binding.friendGoalCheerBtn.setOnClickListener {
            viewModel.sendReaction(false)
        }

        binding.friendGoalMotivateBtn.setOnClickListener {
            viewModel.sendReaction(true)
        }

        binding.friendGoalDetailBackIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.friendGoalSendCommentIv.setOnClickListener {
            val comment = binding.friendGoalCommentEt.text.toString()
            if (comment.isNotEmpty()) {
                sendComment(comment)
            }
        }


        binding.testBtn.setOnClickListener {
            Log.d("popup", "context: ${requireContext()}")
            Log.d("popup", "activity: ${requireActivity()}")
            Log.d("popup", "isAdded: $isAdded")
            Log.d("popup", "isVisible: $isVisible")

            AlertDialog.Builder(requireActivity())
                .setTitle("테스트")
                .setMessage("보이나요?")
                .show()
        }
    }

    private fun setupCombinedChart() {
        val labels = listOf("4월 4주차", "4월 5주차", "이번 주")

        val barEntries = listOf(
            BarEntry(0f, 5f),
            BarEntry(1f, 80f),
            BarEntry(2f, 25f)
        )

        val lineEntries = listOf(
            Entry(0f, 5f),
            Entry(1f, 80f),
            Entry(2f, 25f)
        )

        // Bar 데이터셋
        val barDataSet = BarDataSet(barEntries, "달성률").apply {
            color = "#548DFF".toColorInt()
            valueTextSize = 14f
            valueTextColor = "#5C91FC".toColorInt()
            valueFormatter = PercentFormatter()
        }

        // Line 데이터셋
        val lineDataSet = LineDataSet(lineEntries, "추이").apply {
            color = Color.YELLOW
            circleRadius = 5f
            setCircleColor(Color.YELLOW)
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(false)
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.4f
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData().apply {
            setData(barData)
            setData(lineData)
        }

        chart.apply {
            data = combinedData
            description.isEnabled = false
            legend.isEnabled = false

            // X축
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                setDrawGridLines(false)
                axisLineColor = Color.GRAY

                axisMinimum = -0.5f // ← 첫 번째 바 왼쪽 여유
                axisMaximum = labels.size - 0.5f // ← 마지막 바 오른쪽 여유
            }

            // Y축
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 25f            // 눈금 단위
                setDrawGridLines(false)       // 그리드 라인 제거
                setDrawAxisLine(true)         // 축선 표시
                axisLineColor = Color.GRAY    // 축선 색상
                axisLineWidth = 1f            // 축선 두께
            }
            axisRight.isEnabled = false      // 오른쪽 Y축 사용 안함

            renderer = CustomCombinedChartRenderer(chart, chart.animator, chart.viewPortHandler)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadComment() {
        viewModel.loadComment(createErrorHandler("loadComment"))
    }

    private fun loadTodayFriendTime(){
        viewModel.loadTodayFriendTime(createErrorHandler("loadTodayFriendTime") {
            binding.goalDetailTodayTimerTv.text = it
        })
    }

    private fun loadFriendPhotos() {
        viewModel.loadFriendPhoto(createErrorHandler("loadFriendPhotos") { result ->
            val urls = result.map { it.photoImg }
            setupRecyclerView(urls)
        })
    }
    private fun setupRecyclerView(photoUrls: List<String>) {
        val photoRv = binding.photoRecyclerView
        photoRv.layoutManager = GridLayoutManager(requireContext(), 4) // 4열
        photoRv.adapter = PhotoAdapter(photoUrls)
    }

    private fun setupCommentRv() {
        val commentRv = binding.commentFriendDetailRv
        commentRv.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter = CommentAdapter { view, comment ->
            //showCommentMoreMenu(view, comment)
            showBottomSheetDialog()
        }
        commentRv.adapter = commentAdapter

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.comments.collectLatest { list ->
//                    commentAdapter.submitList(list)  // ⭐ 이 한 줄이 핵심
//                }
//            }
//        }
    }

    private fun pushDummyComment() {
        val dummyComment = GetCommentsResult(
            id = 101,
            content = "와, 정말 유익한 정보네요! 감사합니다.",
            writerId = 50,
            writerNickname = "안드로이드개발자",
            writerProfileImg = "https://example.com/profiles/user50.png",
            parentCommentId = 100,
            parentCommentContent = "이 라이브러리를 사용해보시는 건 어떨까요?",
            parentCommentWriter = "기술멘토",
            reply = true,
            myComment = false
        )
        val dummyList = listOf(dummyComment,dummyComment)
        commentAdapter.submitList(dummyList)
        //TODO : comment dummy (friend goal detail)
    }

    private fun sendComment(comment: String) {
        viewModel.sendComment(comment,createErrorHandler("sendComment") {
            binding.friendGoalCommentEt.text.clear()
            loadComment()
        })
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

//
private fun showCommentMoreMenu(anchorView: View, comment: GetCommentsResult) {
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
        showReportPopup(anchorView, comment)
    }

    popupWindow.showAtLocation(anchorView.rootView, Gravity.CENTER, 0, 0)
}

    private fun showReportPopup(anchorView: View, comment: GetCommentsResult) {
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
                viewModel.postReport(comment.writerId, reason, false)
                popupWindow.dismiss()
            }
        }
        setClick(R.id.report_abuse, "ABUSE_OR_HATE_SPEECH")
        setClick(R.id.report_sexual, "SEXUAL_CONTENT")
        setClick(R.id.report_spam, "SPAM_OR_ADVERTISING")
        setClick(R.id.report_bad, "INAPPROPRIATE_CONTENT")
        setClick(R.id.report_fake, "FRAUD_OR_IMPERSONATION")
        setClick(R.id.report_etc, "OTHER")

        popupWindow.showAtLocation(anchorView.rootView, Gravity.CENTER, 0, 0)
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_photo_delete, null)
        view.findViewById<TextView>(R.id.photo_manage_delete_tv).setOnClickListener {
            Log.d("test","test1")
        }

        view.findViewById<TextView>(R.id.photo_manage_cancel_tv).setOnClickListener {
            Log.d("test","test2")
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
