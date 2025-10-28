package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.R
import com.example.planup.component.ProfileView
import com.example.planup.databinding.FragmentMypageBinding
import com.example.planup.extension.getAppVersion
import com.example.planup.goal.GoalActivity
import com.example.planup.main.MainActivity
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.adapter.ServiceAlertAdapter
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.main.my.ui.common.RouteMenuItemWithArrow
import com.example.planup.main.my.ui.viewmodel.MyPageInfoViewModel
import com.example.planup.main.my.ui.viewmodel.MyPageProfileEditViewModel
import com.example.planup.network.controller.UserController
import com.example.planup.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MypageFragment : Fragment(), ServiceAlertAdapter {
    lateinit var binding: FragmentMypageBinding

    //API 연동
    private lateinit var service: UserController

    //sharedPreferences
    private lateinit var prefs: SharedPreferences

    private val mainSnackbarViewModel: MainSnackbarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyPageNavView(mainSnackbarViewModel)
            }
        }
    }

    private fun init() {
        binding.mypageCl.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.mypageCl.height
                binding.mypageInnerCl.minHeight = height
                binding.mypageCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        //API 서비스
        service = UserController()
        service.setServiceAdapter(this)
    }

    private fun clickListener() {

        binding.mypageMainImageCv.setOnClickListener {
            val intent = Intent(context as MainActivity, GoalActivity::class.java)
            startActivity(intent)
        }

        /*이메일 변경*/
        binding.mypageEmailIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailCheckFragment())
                .commitAllowingStateLoss()
        }
        /*비밀번호 변경*/
        binding.mypagePasswordIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordEmailFragment())
                .commitAllowingStateLoss()
        }
        /*카카오톡 계정 연동*/
        binding.mypageKakaoIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageKakaoFragment())
                .commitAllowingStateLoss()
        }
        //기타 계정 관리
        binding.mypageOtherIv.setOnClickListener {

        }
        //차단 친구 관리
        binding.mypageFriendBlockIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFriendBlockFragment())
                .commitAllowingStateLoss()
        }

        //서비스 알림 수신 토글 끄기
        binding.mypageAlertServiceOnIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility = View.GONE
            binding.mypageAlertServiceOffIv.visibility = View.VISIBLE
        }
        //서비스 알림 수신 토글 켜기
        binding.mypageAlertServiceOffIv.setOnClickListener {
            binding.mypageAlertServiceOnIv.visibility = View.VISIBLE
            binding.mypageAlertServiceOffIv.visibility = View.GONE
        }
        //마케팅 알림 수신 토글 끄기
        binding.mypageAlertBenefitOnIv.setOnClickListener {
            service.notificationAgreementService(false)
        }
        //마케팅 알림 수신 토글 켜기
        binding.mypageAlertBenefitOffIv.setOnClickListener {
            service.notificationAgreementService(true)
        }
    }

    //마케팅 수신 동의하는 경우 팝업 메시지
    private fun alertAgreementPopup(view: Int) {
        val dialog = Dialog(context as MainActivity)
        val today = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
        dialog.setContentView(view)
        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
        //팝업 바깥 부분 터치하는 경우 팝업 끄기
        dialog.setCanceledOnTouchOutside(true)
        //닉네임, 오늘 날짜 출력하기
        dialog.findViewById<TextView>(R.id.popup_benefit_explain_tv).text = getString(
            R.string.popup_benefit_explain,
            prefs.getString("nickname", "null"),
            today
        )
        //확인버틍으로 팝업 끄기
        dialog.findViewById<TextView>(R.id.popup_benefit_ok_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //API 오류에 대한 토스트 메시지 출력
    private fun errorToast(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }

    //마케팅 수신 동의 API 성공
    override fun successServiceSetting(condition: Boolean) {
        if (condition) { //condition==true: 토글 켜기
            binding.mypageAlertBenefitOnIv.visibility = View.VISIBLE
            binding.mypageAlertBenefitOffIv.visibility = View.GONE
            alertAgreementPopup(R.layout.popup_benefit_agree)
        } else { //condition == false: 토글 끄기
            binding.mypageAlertBenefitOnIv.visibility = View.GONE
            binding.mypageAlertBenefitOffIv.visibility = View.VISIBLE
        }
    }

    //마케팅 수신 동의 API 오류
    override fun failServiceSetting(message: String) {
        errorToast(message)
    }

//    //프로필 이미지 API 성공
//    override fun successProfileImage(image: String) {
//        //사용자 프로필 사진
//        Glide.with(context as MainActivity).load(prefs.getString("profileImg", "no-data"))
//            .into(binding.mypageMainImageIv)
//    }
//
//    //프로필 이미지 API 오류
//    override fun failProfileImage(message: String) {
//        errorToast(message)
//    }
}

@Composable
fun MyPageView(
    navigateRoute: (route: MyPageRoute) -> Unit,
    myPageInfoViewModel: MyPageInfoViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        myPageInfoViewModel.fetchUserInfo()
    }
    val email by myPageInfoViewModel.email.collectAsState()
    val profileImage by myPageInfoViewModel.profileImage.collectAsState()

    MyPageViewContent(
        navigateRoute = navigateRoute,
        profileImage = profileImage,
        email = email,
        fetch = myPageInfoViewModel::fetchUserInfo,
        onErrorMsg = {

        }
    )
}

@Composable
@Preview(showBackground = true)
private fun MyPageViewContent(
    navigateRoute: (route: MyPageRoute) -> Unit = {},
    profileImage: String = "",
    email: String = "",
    fetch: () -> Unit = {},
    onErrorMsg: (String) -> Unit = {}
) {
    val context = LocalContext.current
    fun LazyListScope.addSpacer(height: Dp = 28.dp) {
        item {
            Spacer(Modifier.height(height))
        }
    }

    fun LazyListScope.initHeader(
        @StringRes header: Int
    ) {
        item(
            contentType = "header"
        ) {
            RouteHeader(
                title = stringResource(header)
            )
        }
        addSpacer(2.dp)
    }

    fun LazyListScope.initHeaderWithContent(
        @StringRes header: Int,
        content: List<Pair<MyPageRoute, Int>>,
        withSpacer: Boolean = true
    ) {
        initHeader(header)
        content.forEach { (route, title) ->
            item(
                contentType = "route"
            ) {
                RouteMenuItemWithArrow(
                    title = stringResource(title),
                    action = {
                        navigateRoute(route)
                    }
                )
            }
        }
        if (withSpacer) {
            addSpacer()
        }
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        MyPageHeader(
            profileImage = profileImage,
            email = email,
            fetchProfile = fetch,
            onErrorMsg = onErrorMsg
        )
        Spacer(Modifier.height(36.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 13.dp)
        ) {
            initHeaderWithContent(
                header = R.string.mypage_profile,
                content = listOf(
                    MyPageRoute.Profile.EditNickName to R.string.mypage_nickname
                )
            )

            initHeaderWithContent(
                header = R.string.mypage_account,
                content = listOf(
                    MyPageRoute.Account.ChangeEmail to R.string.mypage_email,
                    MyPageRoute.Account.ChangePassword to R.string.mypage_password,
                    MyPageRoute.Account.LinkKakao to R.string.mypage_kakao,
                    MyPageRoute.Account.Other to R.string.mypage_other,
                )
            )

            initHeaderWithContent(
                header = R.string.mypage_friend,
                content = listOf(
                    MyPageRoute.Friend.ManageBlockFriend to R.string.mypage_block,
                )
            )

            initHeader(
                header = R.string.mypage_alert
            )
            item {
                RouteMenuItem(
                    title = stringResource(R.string.mypage_service_alert),
                    rightContent = {

                    }
                )
            }
            item {
                RouteMenuItem(
                    title = stringResource(R.string.mypage_benefit),
                    rightContent = {

                    }
                )
            }
            addSpacer()

            initHeaderWithContent(
                header = R.string.mypage_service,
                content = listOf(
                    MyPageRoute.Service.Policy to R.string.mypage_policy,
                ),
                withSpacer = false
            )
            item {
                RouteMenuItem(
                    title = stringResource(R.string.mypage_version),
                    rightContent = {
                        Text(
                            text = context.getAppVersion(),
                            style = Typography.Semibold_S
                        )
                    }
                )
            }
            addSpacer()
        }
    }
}

@Composable
private fun MyPageHeader(
    profileImage: String,
    email: String,
    fetchProfile: () -> Unit,
    onErrorMsg: (String) -> Unit,
    myPageProfileEditViewModel: MyPageProfileEditViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileView(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            profileUrl = profileImage,
            onNewImageByPhotoPicker = { uri ->
                myPageProfileEditViewModel.setProfileImageByPicker(
                    uri,
                    fetchProfile,
                    onErrorMsg
                )
            },
            onNewImageByCamera = { bitmap ->
                myPageProfileEditViewModel.setProfileImageByPicker(
                    bitmap,
                    fetchProfile,
                    onErrorMsg
                )
            }
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.mypage_cur_email),
                style = Typography.Semibold_SM
            )
            Text(
                text = email,
                style = Typography.Medium_SM
            )
        }
    }
}

@Composable
private fun RouteHeader(
    title: String
) {
    Text(
        text = title,
        style = Typography.Semibold_L
    )
}