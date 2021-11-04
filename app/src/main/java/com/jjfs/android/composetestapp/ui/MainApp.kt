package com.jjfs.android.composetestapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jjfs.android.composetestapp.ui.theme.ComposeTestAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainApp() {
    ComposeTestAppTheme {
//        val systemUiController = rememberSystemUiController()
//        val darkIcons = MaterialTheme.colors.isLight
//        SideEffect {
//            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
//        }
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Main.route

//            val sizeAwareDrawerState = rememberSizeAwareDrawerState(false)
            val coroutineScope = rememberCoroutineScope()
            val loginScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
            )
            val aboutScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
            )

//            ModalDrawer(
//                drawerContent = {
//                    AppDrawer(
//                        currentRoute = currentRoute,
//                        navigateToHome = { navController.navigate(Screen.Main.route) },
//                        showAbout = { showBottomSheet(coroutineScope, aboutScaffoldState) },
//                        closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
//                        modifier = Modifier
//                            .statusBarsPadding()
//                            .navigationBarsPadding()
//                    )
//                },
//                drawerState = sizeAwareDrawerState,
//                gesturesEnabled = true
//            ) {
                Navigation(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    openDrawer = {
//                        coroutineScope.launch { sizeAwareDrawerState.open() }
                    },
                    showBottomSheet = {
                        showBottomSheet(
                            coroutineScope = coroutineScope,
                            bottomSheetScaffoldState = loginScaffoldState
                        )
                    },
                    bottomSheetScaffoldState = loginScaffoldState
                )
            }
//        }
    }
}

@ExperimentalMaterialApi
private fun showBottomSheet(
    coroutineScope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    coroutineScope.launch {
        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
        else bottomSheetScaffoldState.bottomSheetState.collapse()
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}
