package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing
import com.musicorumapp.mobile.ui.theme.SkeletonPrimaryColor
import java.util.*

@Composable
fun Tags(
    tags: List<String>? = null,
    color: Color? = null
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))

            if (tags != null) {
                tags.forEach { TagItem(it, isLast = it === tags.last(), _color = color) }
            } else {
                for (x in 1..10) TagItem(isLast = x === 10)
            }

            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))
        }
    }
}


@Composable
fun TagItem(
    value: String? = null,
    isLast: Boolean = false,
    _color: Color? = null
) {
    val state: Int = remember { Random().nextInt(60) + 40 }
    val color: Color = _color ?: SkeletonPrimaryColor

    val placeholderModifier = Modifier
        .width(state.dp)

    val tagModifier = Modifier
        .background(color.copy(alpha = 0.25f))


    Box(
        modifier = Modifier
            .padding(end = if (isLast) 0.dp else 6.dp)
            .clip(RoundedCornerShape(30.dp))
            .border(1.dp, color, shape = RoundedCornerShape(30.dp))
            .placeholder(
                visible = value == null,
                highlight = PlaceholderHighlight.fade(),
                shape = RoundedCornerShape(30.dp),
            )
            .composed { if (value == null) placeholderModifier else tagModifier }
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .height(22.dp)
    ) {
        Text(
            value.orEmpty(),
            modifier = Modifier
                .offset(y = (-1).dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400, heightDp = 50)
@Composable
fun TagsContentPreview() {
    MusicorumTheme {
        Scaffold {
            Tags(
                listOf(
                    "pop",
                    "kpop",
                    "hiphop",
                    "foo",
                    "bar",
                    "recent",
                    "a long text"
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400, heightDp = 50)
@Composable
fun TagsLoadingPreview() {
    MusicorumTheme {
        Scaffold {
            Tags()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 400, heightDp = 50)
@Composable
fun TagsCheckPreview() {
    MusicorumTheme {
        Scaffold {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                TagItem("Example")
                TagItem("Example", _color = Color.Red)
                TagItem()
                TagItem()
                TagItem("Example", _color = Color.Blue)
                TagItem()
                TagItem("Example", _color = null)
            }
        }
    }
}
