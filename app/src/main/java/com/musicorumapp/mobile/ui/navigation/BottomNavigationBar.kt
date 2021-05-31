package com.musicorumapp.mobile.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.musicorumapp.mobile.ui.theme.AlmostBlack
import com.musicorumapp.mobile.ui.theme.LighterRed

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    print("teste")
    Surface(
//        backgroundColor = AlmostBlack,

    ) {
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
                                      navController.navigate(it.name)
                                  }
                        },
                        label = { Text(title, overflow = TextOverflow.Ellipsis, softWrap = false, fontSize = 10.sp) },
                        alwaysShowLabel = false,

                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview () {
    BottomNavigationBar(navController = rememberNavController())
}