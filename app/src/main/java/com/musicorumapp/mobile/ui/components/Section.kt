package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.ui.theme.SecondaryTextColor

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    val modif = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .composed { modif }
        ) {
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.h5,
                    modifier = if (subTitle != null) Modifier.height(32.dp) else Modifier
                )
                if (subTitle != null) {
                    Text(
                        subTitle,
                        style = MaterialTheme.typography.subtitle1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 11.sp,
                        color = SecondaryTextColor
                    )
                }
            }

            if (onClick != null) {
                Icon(
                    painterResource(id = R.drawable.outline_chevron_right_24),
                    contentDescription = stringResource(id = R.string.view_more)
                )
            }
        }
        content()
    }
}

@Composable
@Preview(showBackground = true)
fun SectionComposePreview() {
    MusicorumTheme {
        Scaffold(
            modifier = Modifier.height(250.dp)
        ) {
            Section(
                title = "Section example",
//                subTitle = "Subtitle example",
                onClick = {}
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22FFFFFF))
                )
            }
        }
    }
}