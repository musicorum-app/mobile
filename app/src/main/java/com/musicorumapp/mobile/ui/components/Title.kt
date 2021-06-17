package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.ui.navigation.ComposableRoutes
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons
import com.musicorumapp.mobile.ui.theme.MusicorumTheme

@Composable
fun Title(
    text: String,
    topPadding: Boolean = true,
    showSearch: Boolean = false,
    showSettings: Boolean = false,
    showBackButton: Boolean = false
) {

    val navController = LocalNavigationContext.current.navigationController

    val padding = Modifier

    if (topPadding) padding.height(8.dp)

    Box(modifier = Modifier.statusBarsPadding().composed { padding }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
               if (showBackButton) {
                   IconButton(onClick = {
                       if (navController?.currentBackStackEntry != null) {
                           navController.popBackStack()
                       }
                   }) {
                       Icon(
                           AppMaterialIcons.ArrowBack,
                           contentDescription = stringResource(id = R.string.back)
                       )
                   }
               }

                Text(
                    text,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                if (showSearch) {
                    IconButton(onClick = {
                        navController?.navigate(ComposableRoutes.Search)
                    }) {
                        Icon(
                            AppMaterialIcons.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                }
                if (showSettings) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            AppMaterialIcons.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    MusicorumTheme {
        Scaffold {
            Title(text = "Title text")
        }
    }
}