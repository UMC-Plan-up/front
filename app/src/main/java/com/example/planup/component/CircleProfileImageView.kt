package com.example.planup.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.example.planup.R

@Composable
fun CircleProfileImageView(
    modifier: Modifier,
    profileImage : Any?
) {
    AsyncImage(
        modifier = modifier
            .clip(CircleShape),
        model = profileImage,
        placeholder = painterResource(R.drawable.profile_image),
        error = painterResource(R.drawable.profile_image),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}