package com.example.planup.main.my.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.planup.R
import com.example.planup.component.PlanUpSwitch
import com.example.planup.databinding.FragmentMypageBinding
import com.example.planup.extension.getAppVersion
import com.example.planup.goal.GoalActivity
import com.example.planup.main.MainActivity
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.main.my.ui.common.RouteMenuItemWithArrow
import com.example.planup.main.my.ui.viewmodel.MyPageInfoViewModel
import com.example.planup.main.my.ui.viewmodel.MyPageProfileEditViewModel
import com.example.planup.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding

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
        //차단 친구 관리
        binding.mypageFriendBlockIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFriendBlockFragment())
                .commitAllowingStateLoss()
        }
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
    myPageInfoViewModel: MyPageInfoViewModel = hiltViewModel(),
    mainSnackbarViewModel: MainSnackbarViewModel
) {
    LaunchedEffect(true) {
        myPageInfoViewModel.fetchUserInfo()
    }

    val context = LocalContext.current

    val email by myPageInfoViewModel.email.collectAsState()
    val nickName by myPageInfoViewModel.nickName.collectAsState()
    val profileImage by myPageInfoViewModel.profileImage.collectAsState()
    val serviceNotification by myPageInfoViewModel.notificationLocal.collectAsState()
    val marketingNotification by myPageInfoViewModel.marketingNotification.collectAsState()

    val updateMarketingNotification = { newNotification :Boolean ->
        myPageInfoViewModel.updateNotificationMarketing(
            notificationMarketing = newNotification,
            onSuccess = {
                navigateRoute(
                    MyPageRoute.NotificationMarketing(
                        isAgree = newNotification,
                        nickName = nickName,
                        date = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date())
                    )
                )
            },
            onFail = mainSnackbarViewModel::updateMessage
        )
    }


    /**
     * 알림 권한 요청
     */
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                myPageInfoViewModel.updateNotificationLocal(true)
            } else {
                Toast.makeText(context, "알림 권한 설정이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    val notificationMarketingPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                updateMarketingNotification(true)
            } else {
                Toast.makeText(context, "알림 권한 설정이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }


    MyPageViewContent(
        navigateRoute = navigateRoute,
        profileImage = profileImage,
        email = email,
        serviceNotification = serviceNotification,
        marketingNotification = marketingNotification,
        updateServiceNotification = { newNotification ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //33 이상은 먼저 POST_NOTIFICATIONS 권한 체크를 먼저 진행한다.
                //이가 선행 되지 않으면, true가 되더라도 실제 알림 수신을 트리거 할 수 없다.
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    myPageInfoViewModel.updateNotificationLocal(newNotification)
                } else {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                //33 미만은 별도 권한 없이 작동 하기 때문에,, 그냥 true,false만 처리해서, 받아주면 된다.
                myPageInfoViewModel.updateNotificationLocal(newNotification)
            }
        },
        updateMarketingNotification = { newNotification ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //33 이상은 먼저 POST_NOTIFICATIONS 권한 체크를 먼저 진행한다.
                //이가 선행 되지 않으면, true가 되더라도 실제 알림 수신을 트리거 할 수 없다.
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    updateMarketingNotification(newNotification)
                } else {
                    notificationMarketingPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                //33 미만은 별도 권한 없이 작동 하기 때문에,, 그냥 true,false만 처리해서, 받아주면 된다.
                updateMarketingNotification(newNotification)
            }
        },
        fetch = myPageInfoViewModel::fetchUserInfo,
        onErrorMsg = mainSnackbarViewModel::updateMessage
    )
}

@Composable
private fun MyPageViewContent(
    navigateRoute: (route: MyPageRoute) -> Unit,
    profileImage: String,
    email: String,
    serviceNotification: Boolean,
    marketingNotification: Boolean,
    updateServiceNotification: (Boolean) -> Unit,
    updateMarketingNotification: (Boolean) -> Unit,
    fetch: () -> Unit,
    onErrorMsg: (String) -> Unit
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
                        PlanUpSwitch(
                            checked = serviceNotification,
                            onCheckedChange = updateServiceNotification
                        )
                    }
                )
            }
            item {
                RouteMenuItem(
                    title = stringResource(R.string.mypage_benefit),
                    rightContent = {
                        PlanUpSwitch(
                            checked = marketingNotification,
                            onCheckedChange = updateMarketingNotification
                        )
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
    var openPopup by remember {
        mutableStateOf(false)
    }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                myPageProfileEditViewModel.setProfileImageByPicker(
                    imageUri = uri,
                    onSuccess = fetchProfile,
                    onFail = onErrorMsg
                )
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                myPageProfileEditViewModel.setProfileImageCamera(
                    onSuccess = fetchProfile,
                    onFail = onErrorMsg
                )
            }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = profileImage,
                placeholder = painterResource(R.drawable.profile_image),
                error = painterResource(R.drawable.profile_image),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd),
            ) {
                IconButton(
                    onClick = {
                        openPopup = true
                    },
                    modifier = Modifier
                        .size(20.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.badge_rewrite),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.BottomEnd),
                    expanded = openPopup,
                    onDismissRequest = { openPopup = false },
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "사진 보관함",
                                style = Typography.Medium_SM
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_item_album),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "사진 찍기",
                                style = Typography.Medium_SM
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_item_camera),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            cameraLauncher.launch(myPageProfileEditViewModel.makeCameraTempFileUri())
                        }
                    )
                }
            }

        }

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