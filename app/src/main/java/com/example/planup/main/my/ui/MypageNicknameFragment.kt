package com.example.planup.main.my.ui

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.component.PlanUpButton
import com.example.planup.component.TopHeader
import com.example.planup.databinding.FragmentMypageNicknameBinding
import com.example.planup.main.my.adapter.NicknameChangeAdapter
import com.example.planup.main.my.ui.common.RoutePageDefault
import com.example.planup.main.my.ui.viewmodel.MyPageNickNameEditViewModel
import com.example.planup.network.controller.UserController
import com.example.planup.theme.Black400
import com.example.planup.theme.Red200
import com.example.planup.theme.SemanticR1
import kotlin.math.sin

//마이페이지의 닉네임 변경 프레그먼트
class MypageNicknameFragment : Fragment() {

    lateinit var binding: FragmentMypageNicknameBinding
    lateinit var prefs: SharedPreferences
    lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageNicknameBinding.inflate(inflater, container, false)
        init()
        clickListener()
        textListener()
        return binding.root
    }

    //초기 세팅
    private fun init() {
        binding.mypageNicknameCl.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.mypageNicknameCl.height
                binding.mypageNicknameInnerCl.minHeight = height
                binding.mypageNicknameCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        prefs = (context as MainActivity).getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = prefs.edit()
    }

    private fun clickListener() {
        //뒤로 가기
        binding.nicknameBackIv.setOnClickListener {
            (context as MainActivity).navigateToFragment(MypageFragment())
        }
        //완료 버튼 클릭: 마이페이지 화면으로 이동
        binding.nicknameCompleteBtn.setOnClickListener {
            val newNickname = prefs.getString("nickname", "null")
            val prevNickname = binding.nicknameEt.text.toString()

            if (prevNickname == newNickname?.removeSurrounding("\"")
                && newNickname != "null"
            ) {
                val errorColor = ContextCompat.getColor(context, R.color.semanticR1)
                binding.nickNameErrorTv.setText(R.string.error_already_nickname)
                binding.nickNameLv.setBackgroundColor(errorColor)
                return@setOnClickListener
            }
            val nicknameService = UserController()
        }
    }

    /*닉네임 검사*/
    private fun textListener() {
        val errorColor = ContextCompat.getColor(context, R.color.red_200)
        val normalColor = ContextCompat.getColor(context, R.color.black_400)
        binding.nicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                if (binding.nicknameEt.text.toString().length in 1..20) {
                    //정상
                    binding.nickNameErrorTv.setText(null)
                    binding.nickNameLv.setBackgroundColor(normalColor)
                    binding.nicknameCompleteBtn.isActivated = true
                } else if (20 < binding.nicknameEt.text.toString().length) {
                    //20자 초과
                    binding.nickNameErrorTv.setText(R.string.error_under_twenty_word)
                    binding.nickNameLv.setBackgroundColor(errorColor)
                    binding.nicknameCompleteBtn.isActivated = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    //닉네임 변경 성공
    fun successNicknameChange(nickname: String) {
        editor.putString("nickname", nickname)
        editor.apply()
        (context as MainActivity).navigateToFragment(MypageFragment())
    }

    //닉네임 변경 오류
    fun failNicknameChange(message: String) {
        val errorColor = ContextCompat.getColor(context, R.color.semanticR1)
        binding.nickNameErrorTv.setText(R.string.error_already_nickname)
        binding.nickNameLv.setBackgroundColor(errorColor)

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
fun MyPageNickNamEditView(
    onBack: () -> Unit,
    myPageNickNameEditViewModel: MyPageNickNameEditViewModel = hiltViewModel()
) {
    var newName by rememberSaveable {
        mutableStateOf("")
    }

    var isError by rememberSaveable { mutableStateOf(false) }
    val charLimit = 20

    val completeEnabled by remember {
        derivedStateOf { newName.isNotEmpty() && !isError }
    }

    LaunchedEffect(newName) {
        isError = newName.length > charLimit
    }

    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_nickname)
    ) {
        Spacer(Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(
                    text = stringResource(R.string.nickname_title)
                )
                BasicTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                    },
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Column {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(41.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                                if (newName.isEmpty()) {
                                    Text(
                                        text = "닉네임을 입력하세요"
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = if (isError) {
                                    Red200
                                } else {
                                    Black400
                                }
                            )
                            if (isError) {
                                Text(
                                    text = stringResource(R.string.error_under_twenty_word),
                                    color = SemanticR1
                                )
                            }
                        }
                    }
                )
            }
            PlanUpButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.btn_complete),
                enabled = completeEnabled
            ) {
                myPageNickNameEditViewModel.changeNickname(
                    nickName = newName,
                    onSuccess = {
                        onBack()
                    }
                )
            }
        }
    }
}