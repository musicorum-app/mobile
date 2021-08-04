package com.musicorumapp.mobile.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import com.musicorumapp.mobile.Constants

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    if (pagesWithBottomBar.contains(currentRoute(navController = navController).orEmpty())) {
        Surface {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                ) {
                    mainPages.forEach {
                        val title = stringResource(id = it.titleResource)
                        val currentRoute = currentRoute(navController = navController)
                        val root = navController.graph.startDestinationRoute.orEmpty()
                        Log.d(Constants.LOG_TAG, root)
                        val selected = currentRoute == it.name

                        BottomNavigationItem(
                            selected = selected,

                            icon = {
                                if (it.icon.material != null) Icon(
                                    imageVector = it.icon.material,
                                    contentDescription = title
                                )
                                else if (it.icon.drawable != null) Icon(
                                    painter = painterResource(id = it.icon.drawable),
                                    contentDescription = title
                                )
                            },
                            onClick = {
                                if (!selected) {
                                    navController.navigate(it.name) {
                                        popUpTo(navController.graph.startDestinationRoute.orEmpty()) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            label = {
                                Text(
                                    title,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
//                            fontSize = 10.sp
                                )
                            },
                            alwaysShowLabel = false,

                            )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = rememberNavController())
}