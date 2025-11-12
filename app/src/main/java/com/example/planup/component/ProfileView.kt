package com.example.planup.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.theme.Typography

@Composable
fun ProfileView(
    modifier: Modifier,
    profileUrl: String,
    onNewImageByPhotoPicker: (Uri) -> Unit,
    onNewImageByCamera: (Bitmap) -> Unit
) {
    var openPopup by remember {
        mutableStateOf(false)
    }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onNewImageByPhotoPicker(it) }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { onNewImageByCamera(it) }
        }

    Box(
        modifier = Modifier
            .size(60.dp)
            .then(modifier)
    ) {
        CircleProfileImageView(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            profileImage = profileUrl
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
                            style = com.example.planup.theme.Typography.Medium_SM
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
                        openPopup = false
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
                        cameraLauncher.launch(null)
                        openPopup = false
                    }
                )
            }
        }

    }
}