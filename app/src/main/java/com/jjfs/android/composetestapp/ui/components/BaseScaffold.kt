package com.jjfs.android.composetestapp.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jjfs.android.composetestapp.R
import com.jjfs.android.composetestapp.ui.screens.auth.LoginScreen
import com.jjfs.android.composetestapp.ui.connectivityState
import com.jjfs.android.composetestapp.ui.utils.ConnectionState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    showBottomSheet: () -> Unit,
    onDismissCallback: () -> Unit,
    openDrawer: () -> Unit,
    topBar: @Composable() ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    /** should probably hoist this from main activity */
//    val connection by connectivityState()
    val isConnected = true //connection === ConnectionState.Available
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            LoginScreen(
                scaffoldState = bottomSheetScaffoldState,
                showBottomSheet = showBottomSheet,
                onDismiss = onDismissCallback
            )
        },
        sheetPeekHeight = 0.dp,
    ) {
        Scaffold(
            topBar = { TopAppBar(connection = isConnected, openDrawer = openDrawer) }
        ) {
            content()
        }
    }
}

@Composable
private fun TopAppBar(
    elevation: Dp = 8.dp,
    openDrawer: () -> Unit = {},
    connection: Boolean
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                    tint = MaterialTheme.colors.background
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
        },
        backgroundColor = if(connection) MaterialTheme.colors.primary else Color.Red,
        elevation = elevation
    )
}
