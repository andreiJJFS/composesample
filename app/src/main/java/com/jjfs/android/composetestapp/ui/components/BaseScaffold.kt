package com.jjfs.android.composetestapp.ui.components

import android.util.Log
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jjfs.android.composetestapp.R
import com.jjfs.android.composetestapp.ui.screens.auth.LoginScreen
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    showBottomSheet: () -> Unit,
    onDismissCallback: () -> Unit = {},
    openDrawer: () -> Unit = {},
    viewModel: CommonViewModel,
    onNav: (String) -> Unit,
    scaffoldState: ScaffoldState,
    topBar: @Composable() ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    /** should probably hoist this from main activity */
//    val connection by connectivityState()
    val isConnected = true //connection === ConnectionState.Available

    LaunchedEffect(key1 = true) {
        viewModel.eventChannel.collectLatest { event ->
            when (event) {
                is Event.Navigate<*> -> {
                    if(event.args == null) onNav(event.screen.route)
                    else onNav("${event.screen.route}/${event.args}")
                }
                is Event.OpenLogin -> showBottomSheet()
                is Event.ShowSnackBar ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Swipe!"
                    )
                else -> Unit
            }
        }
    }

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
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(connection = isConnected, openDrawer = openDrawer)
            }
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
