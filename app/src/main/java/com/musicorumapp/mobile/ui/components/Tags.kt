package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing

@Composable
fun Tags(
    tags: List<String>? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))

        if (tags != null) {
            tags.forEach { TagItem(it) }
        } else {
            for (x in 1..10) TagItem()
        }

        Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))
    }
}


@Composable
fun TagItem(
    value: String? = null
) {
    Box(
        modifier = Modifier
            .placeholder(
                visible = value != null,
                highlight = PlaceholderHighlight.fade()
            )
    ) {
        Text(value.orEmpty())
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 100)
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

@Preview(showBackground = true, widthDp = 400, heightDp = 100)
@Composable
fun TagsLoadingPreview() {
    MusicorumTheme {
        Scaffold {
            Tags()
        }
    }
}
