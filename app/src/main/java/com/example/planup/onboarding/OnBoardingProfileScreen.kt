package com.example.planup.onboarding

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.ProfileView
import com.example.planup.component.button.PlanUpButton
import com.example.planup.onboarding.component.OnBoardingTextField
import com.example.planup.onboarding.model.GenderModel
import com.example.planup.theme.Black200
import com.example.planup.theme.Black300
import com.example.planup.theme.Blue200
import com.example.planup.theme.SemanticB4
import com.example.planup.theme.Typography

@Composable
fun OnBoardingProfileScreen(
    modifier: Modifier = Modifier,
    state: OnBoardingState,
    onNameChanged: (String) -> Unit,
    onNicknameChanged: (String) -> Unit,
    onGenderChanged: (GenderModel) -> Unit,
    onNewImageByCamera: (Bitmap) -> Unit,
    onNewImageByPhotoPicker: (Uri) -> Unit,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    onClickNicknameDuplication: () -> Unit,
    onNext: () -> Unit
) {
    val nameState = rememberTextFieldState(state.name)
    val nicknameState = rememberTextFieldState(state.nickname)

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp),
    ) {
        ProfileBody(
            modifier = Modifier
                .padding(top = 42.dp, bottom = 12.dp)
                .fillMaxWidth()
                .weight(1.0f),
            state = state,
            nameState = nameState,
            nicknameState = nicknameState,
            validateNameFormat = onNameChanged,
            validateNickNameFormat = onNicknameChanged,
            onGenderChanged = onGenderChanged,
            onNewImageByCamera = onNewImageByCamera,
            onNewImageByPhotoPicker = onNewImageByPhotoPicker,
            onYearChanged = onYearChanged,
            onMonthChanged = onMonthChanged,
            onDayChanged = onDayChanged,
            onClickDuplication = onClickNicknameDuplication
        )
        ProfileTail(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp),
            onNext = onNext
        )
    }
}

@Composable
private fun ProfileBody(
    state: OnBoardingState,
    nameState: TextFieldState,
    nicknameState: TextFieldState,
    validateNameFormat: (String) -> Unit,
    validateNickNameFormat: (String) -> Unit,
    onGenderChanged: (GenderModel) -> Unit,
    onNewImageByCamera: (Bitmap) -> Unit,
    onNewImageByPhotoPicker: (Uri) -> Unit,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    onClickDuplication: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNameFocused by remember { mutableStateOf(false) }
    val isNameEmpty = isNameFocused && nameState.text.isBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileView(
            modifier = Modifier
                .size(100.dp),
            profileUrl = state.profileImage,
            onNewImageByPhotoPicker = onNewImageByPhotoPicker,
            onNewImageByCamera = onNewImageByCamera
        )
        Column(
            modifier = Modifier
                .padding(top = 44.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = stringResource(R.string.profile_name_label),
                style = Typography.Regular_L
            )
            OnBoardingTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .onFocusChanged { focusState ->
                        isNameFocused = focusState.hasFocus
                    },
                state = nameState,
                placeHolder = stringResource(R.string.profile_name_input_hint),
                inputTransformation = {
                    validateNameFormat(toString())
                }
            )

            Column(
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        start = 12.dp
                    )
            ) {
                if (isNameEmpty) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_name_error_required),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
                if (!state.isValidNameLength) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_name_error_length),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
                if (state.isNameContainsSpecialChar && state.name.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_name_error_special_char),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
            }

            Text(
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp),
                text = stringResource(R.string.profile_gender_label),
                style = Typography.Regular_L
            )

            GenderRadioGroup(
                modifier = Modifier
                    .padding(top = 8.dp),
                selected = state.gender,
                onGenderChanged = onGenderChanged
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 8.dp
                    ),
                text = stringResource(R.string.profile_birth_label),
                style = Typography.Regular_L
            )

            BirthDropBoxGroup(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                years = state.years,
                months = state.months,
                days = state.days,
                year = state.year,
                month = state.month,
                day = state.day,
                onYearChanged = onYearChanged,
                onMonthChanged = onMonthChanged,
                onDayChanged = onDayChanged
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 8.dp
                    ),
                text = stringResource(R.string.profile_nickname_label),
                style = Typography.Regular_L
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OnBoardingTextField(
                    modifier = Modifier
                        .weight(1.0f),
                    state = nicknameState,
                    placeHolder = stringResource(R.string.profile_nickname_input_hint),
                    inputTransformation = {
                        validateNickNameFormat(toString())
                    }
                )

                OutlinedButton(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        disabledContainerColor = SemanticB4,
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Blue200
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 6.dp
                    ),
                    onClick = onClickDuplication
                ) {
                    Text(
                        text = stringResource(R.string.btn_duplication_check),
                        style = Typography.Medium_S.copy(color = Blue200)
                    )
                }

            }

            Column(
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        start = 12.dp
                    )
            ) {
                if (!state.isValidNickNameLength) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_nickname_error_length),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
                if(state.isNickNameContainsSpecialChar && nicknameState.text.isNotBlank()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_name_error_special_char),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
                if (state.isDuplicateNickName && nicknameState.text.isNotBlank()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_nickname_error_duplicate),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
                if (state.isAvailableNickName) {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = stringResource(R.string.profile_nickname_available),
                        style = Typography.Medium_XS,
                        color = Blue200
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTail(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        PlanUpButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.btn_next),
            onClick = onNext
        )
    }
}

@Composable
private fun GenderRadioGroup(
    selected: GenderModel?,
    onGenderChanged: (GenderModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedButton(
            onClick = { onGenderChanged(GenderModel.Male) },
            modifier = Modifier
                .height(36.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                disabledContainerColor = SemanticB4,
            ),
            border = BorderStroke(1.dp, if (selected == GenderModel.Male) Blue200 else SemanticB4),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 8.dp
            )
        ) {
            Text(
                text = stringResource(R.string.profile_gender_male),
                style = Typography.Medium_SM,
                color = if (selected == GenderModel.Male) Blue200 else Black300
            )
        }

        OutlinedButton(
            onClick = { onGenderChanged(GenderModel.Female) },
            modifier = Modifier
                .height(36.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                disabledContainerColor = SemanticB4,
            ),
            border = BorderStroke(
                1.dp,
                if (selected == GenderModel.Female) Blue200 else SemanticB4
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 8.dp
            )
        ) {
            Text(
                text = stringResource(R.string.profile_gender_female),
                style = Typography.Medium_SM,
                color = if (selected == GenderModel.Female) Blue200 else Black300
            )
        }
    }
}

@Composable
private fun BirthDropBoxGroup(
    years: List<Int>,
    months: List<Int>,
    days: List<Int>,
    year: Int,
    month: Int,
    day: Int,
    onYearChanged: (Int) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onDayChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isYearMenuOpen by remember { mutableStateOf(false) }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isDayMenuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        BirthDropBox(
            modifier = Modifier.width(80.dp),
            items = years,
            text = if (year == 0) "" else year.toString(),
            postfix = "년",
            placeHolder = stringResource(R.string.profile_birth_year),
            onClickBox = { isYearMenuOpen = true },
            onClickItem = {
                isYearMenuOpen = false
                onYearChanged(it)
            },
            onDismissRequest = { isYearMenuOpen = false },
            isExpended = isYearMenuOpen
        )

        BirthDropBox(
            modifier = Modifier.width(60.dp),
            items = months,
            text = if (month == 0) "" else month.toString(),
            postfix = "월",
            placeHolder = stringResource(R.string.profile_birth_month),
            onClickBox = { isMonthMenuOpen = true },
            onClickItem = {
                isMonthMenuOpen = false
                onMonthChanged(it)
            },
            onDismissRequest = { isMonthMenuOpen = false },
            isExpended = isMonthMenuOpen
        )

        BirthDropBox(
            modifier = Modifier.width(60.dp),
            items = days,
            text = if (day == 0) "" else day.toString(),
            postfix = "일",
            placeHolder = stringResource(R.string.profile_birth_day),
            onClickBox = { isDayMenuOpen = true },
            onClickItem = {
                isDayMenuOpen = false
                onDayChanged(it)
            },
            onDismissRequest = { isDayMenuOpen = false },
            isExpended = isDayMenuOpen
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDropBox(
    items: List<Int>,
    text: String,
    postfix: String,
    placeHolder: String,
    onClickBox: () -> Unit,
    onClickItem: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isExpended: Boolean = false
) {
    ExposedDropdownMenuBox(
        modifier = modifier
            .height(36.dp),
        expanded = isExpended,
        onExpandedChange = { },
    ) {
        OutlinedButton(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .wrapContentWidth(),
            onClick = onClickBox,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                disabledContainerColor = Black200,
            ),
            border = BorderStroke(1.dp, Black200),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(
                8.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1.0f),
                    text = if (text.isBlank()) placeHolder else "$text$postfix",
                    style = Typography.Medium_S,
                )
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
                    contentDescription = null
                )
            }
        }

        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = onDismissRequest,
            containerColor = Color.White,
            border = BorderStroke(1.dp, Black200)
        ) {
            items.forEach {
                DropdownMenuItem(
                    modifier = Modifier
                        .height(22.dp),
                    text = {
                        Text(
                            text = "$it"
                        )
                    },
                    onClick = {
                        onClickItem(it)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun OnBoardingProfileScreenPreview() {
    OnBoardingProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        state = OnBoardingState(),
        onNameChanged = {},
        onNicknameChanged = {},
        onGenderChanged = {},
        onNewImageByCamera = {},
        onNewImageByPhotoPicker = {},
        onYearChanged = {},
        onMonthChanged = {},
        onDayChanged = {},
        onClickNicknameDuplication = {},
        onNext = {},
    )
}