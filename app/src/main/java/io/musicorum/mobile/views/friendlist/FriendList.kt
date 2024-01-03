package io.musicorum.mobile.views.friendlist

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackSheet
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.Rive

@Composable
fun FriendList(vm: FriendListViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = { AppBar() },
        snackbarHost = { SnackbarHost(vm.snackbarHostState) }
    ) { pv ->
        Column(Modifier.padding(pv)) {
            if (state.loading) {
                CenteredLoadingSpinner()
            } else {
                Text(
                    "${state.friends.size} friends",
                    modifier = Modifier.padding(start = 15.dp, bottom = 15.dp),
                    color = ContentSecondary
                )
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(160.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.friends) { user ->
                        val activityViewModel: FriendActivityViewModel = viewModel(key = user.name)
                        val isPinned = user.name in state.pinnedUsers
                        FriendActivity(user = user, isPinned = isPinned, vm = activityViewModel) {
                            if (isPinned) vm.unpinUser(user)
                            else vm.pinUser(user)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar() {
    val nav = LocalNavigation.current
    MediumTopAppBar(title = { Text("Friends") }, navigationIcon = {
        IconButton(onClick = { nav?.popBackStack() }) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FriendActivity(
    vm: FriendActivityViewModel,
    user: UserData,
    isPinned: Boolean,
    onPin: () -> Unit
) {
    val state by vm.state.collectAsState()
    val reqModel = defaultImageRequestBuilder(url = user.bestImageUrl)
    val showBottomSheet = remember { mutableStateOf(false) }
    val showTrackBottomSheet = remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val trackRowISource = remember { MutableInteractionSource() }
    val nav = LocalNavigation.current


    LaunchedEffect(key1 = Unit) {
        vm.fetchActivity(user.name)
    }

    if (showBottomSheet.value) {
        UserBottomSheet(
            userData = user,
            isPinned = isPinned,
            onDismiss = { showBottomSheet.value = false }
        ) {
            showBottomSheet.value = false
            onPin()
        }
    }

    if (showTrackBottomSheet.value) {
        state.track?.let {
            TrackSheet(track = it, showTrackBottomSheet) {}
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .combinedClickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = {
                    nav?.navigate(Routes.user(user.name))
                },
                onLongClick = {
                    showBottomSheet.value = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            .consumeWindowInsets(WindowInsets.navigationBars)
    ) {
        AsyncImage(
            model = reqModel,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(105.dp)
                .indication(interactionSource, LocalIndication.current)
        )
        Text(
            text = user.name,
            style = Typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .placeholder(
                    visible = state.loading,
                    color = KindaBlack,
                    shape = RoundedCornerShape(3.dp),
                    highlight = PlaceholderHighlight.shimmer()
                )
                .clip(RoundedCornerShape(3.dp))
                .combinedClickable(
                    indication = LocalIndication.current,
                    interactionSource = trackRowISource,
                    onClick = {
                        state.track?.let {
                            nav?.navigate(Routes.track(NavigationTrack(it.name, it.artist.name)))
                        }
                    },
                    onLongClick = { showTrackBottomSheet.value = true },
                )
        ) {
            val albumModel =
                defaultImageRequestBuilder(url = state.track?.bestImageUrl, PlaceholderType.ALBUM)
            AsyncImage(
                model = albumModel, contentDescription = null, modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .size(40.dp)
                    .indication(trackRowISource, LocalIndication.current),
                colorFilter = if (!state.nowPlaying) {
                    ColorFilter.colorMatrix(ColorMatrix().apply { this.setToSaturation(0F) })
                } else null
            )
            Column(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.nowPlaying) {
                        Rive.AnimationFor(id = R.raw.nowplaying, modifier = Modifier.size(10.dp))
                    }
                    Text(
                        text = state.track?.name ?: "Unknown",
                        style = Typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (state.nowPlaying) {
                    Text(
                        text = state.track?.artist?.name ?: "Unknown",
                        style = Typography.labelSmall,
                        color = ContentSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    val now = System.currentTimeMillis()
                    state.track?.let {
                        val string = DateUtils.getRelativeTimeSpanString(
                            it.date!!.uts.toLong() * 1000,
                            now,
                            DateUtils.SECOND_IN_MILLIS
                        ).toString()
                        Text(
                            text = string,
                            style = Typography.labelSmall,
                            color = ContentSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}