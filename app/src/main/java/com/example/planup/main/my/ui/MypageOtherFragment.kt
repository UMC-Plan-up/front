package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.component.PlanUpBorderButton
import com.example.planup.component.PlanUpButton
import com.example.planup.databinding.FragmentMypageOtherBinding
import com.example.planup.login.ui.LoginActivityNew
import com.example.planup.main.MainActivity
import com.example.planup.main.my.adapter.LogoutAdapter
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.network.controller.UserController
import com.example.planup.theme.Typography

class MypageOtherFragment : Fragment(), LogoutAdapter {
    lateinit var binding: FragmentMypageOtherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageOtherBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    private fun init() {
        binding.mypageOtherCl.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.mypageOtherCl.height
                binding.mypageOtherInnerCl.minHeight = height
                binding.mypageOtherCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.otherBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }

        /*로그아웃*/
        binding.otherLogOutCl.setOnClickListener {
            makePopup()
        }
        /*회원 탈퇴*/
        binding.otherDeleteAccountCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageDeleteAccountFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun makePopup() {
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_logout)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)

            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        /*취소 버튼 클릭 시 팝업 사라짐*/
        dialog.findViewById<View>(R.id.btn_logout_no_tv).setOnClickListener {
            dialog.dismiss()
        }

        /*확인 버튼 클릭 시 로그인 화면으로 이동*/
        dialog.findViewById<View>(R.id.btn_logout_ok_tv).setOnClickListener {
            val logoutService = UserController()
            logoutService.setLogoutAdapter(this)
            logoutService.logoutService()
        }
        dialog.show()
    }

    override fun successLogout() {
        val intent = Intent(context as MainActivity, LoginActivityNew::class.java)
        startActivity(intent)
    }

    override fun failLogout(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template, null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 300)
        toast.show()
    }
}

@Composable
fun MyPageOtherView(
    onBack: () -> Unit,
    navigateDelete: () -> Unit,
) {
    var showLogoutDialog by rememberSaveable() {
        mutableStateOf(false)
    }
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.category)
    ) {
        Spacer(Modifier.height(20.dp))
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_logout),
            action = {
                showLogoutDialog = true
            }
        )
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_delete),
            action = navigateDelete
        )
    }
    if (showLogoutDialog) {
        LogoutDialog(
            onDismissRequest = { showLogoutDialog = false },
            logout = {

            }
        )
    }
}


@Composable
private fun LogoutDialog(
    onDismissRequest: () -> Unit,
    logout: () -> Unit = {}
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest
    ) {
        OutlinedCard(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp,androidx.compose.ui.graphics.Color(0xffdadada)),
            colors = CardDefaults.outlinedCardColors(
                containerColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .height(140.dp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.popup_ask_logout),
                        style = Typography.Medium_SM
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlanUpBorderButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.btn_cancel),
                        onClick = onDismissRequest
                    )
                    PlanUpButton(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.btn_ok),
                        onClick = logout
                    )
                }
            }
        }
    }
}