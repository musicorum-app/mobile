package io.musicorum.mobile.components.skeletons

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer
import io.musicorum.mobile.components.DetailHeaderDefaults
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.SkeletonPrimaryColor
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor

private val skeletonShape = RoundedCornerShape(6.dp)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun DetailLoadingSkeleton(
    coverShape: Shape,
    showSubtitle: Boolean,
    showContext: Boolean = false
) {
    val appBarState = rememberTopAppBarState(
        initialContentOffset = DetailHeaderDefaults.initialAppBarContentOffset
    )
    val appBarBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)

    Scaffold(
        topBar = {
            MusicorumTopBar(
                text = "",
                scrollBehavior = appBarBehavior,
                fadeable = true
            ) {}
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(KindaBlack)
                .nestedScroll(appBarBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .clearAndSetSemantics { }
        ) {
            DetailHeaderSkeleton(coverShape)
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(
                Modifier
                    .fillMaxWidth(0.68f)
                    .height(40.dp)
            )
            if (showSubtitle) {
                Spacer(Modifier.height(6.dp))
                SkeletonBlock(
                    Modifier
                        .fillMaxWidth(0.36f)
                        .height(20.dp)
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 18.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                repeat(3) {
                    SkeletonBlock(
                        Modifier
                            .weight(1f)
                            .height(54.dp)
                    )
                }
            }
            if (showContext) {
                HorizontalDivider(Modifier.padding(vertical = 20.dp))
                ContextRowSkeleton()
            }
        }
    }
}

@Composable
private fun ContextRowSkeleton() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        ContextItemSkeleton(
            imageShape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f)
        )
        ContextItemSkeleton(
            imageShape = CircleShape,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ContextItemSkeleton(imageShape: Shape, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        SkeletonBlock(
            Modifier
                .fillMaxWidth(0.48f)
                .height(14.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBlock(Modifier.size(44.dp), imageShape)
            SkeletonBlock(
                Modifier
                    .weight(1f)
                    .height(24.dp)
            )
        }
    }
}

@Composable
private fun DetailHeaderSkeleton(coverShape: Shape) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(DetailHeaderDefaults.height)
    ) {
        SkeletonBlock(
            Modifier
                .padding(top = DetailHeaderDefaults.coverTopPadding)
                .size(DetailHeaderDefaults.coverSize),
            coverShape
        )
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier, shape: Shape = skeletonShape) {
    Box(
        modifier = modifier
            .placeholder(
                visible = true,
                color = SkeletonPrimaryColor,
                highlight = PlaceholderHighlight.shimmer(SkeletonSecondaryColor),
                shape = shape
            )
    )
}
