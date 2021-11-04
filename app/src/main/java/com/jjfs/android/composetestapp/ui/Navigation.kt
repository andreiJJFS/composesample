package com.jjfs.android.composetestapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jjfs.android.composetestapp.ui.screens.detail.DetailScreen
import com.jjfs.android.composetestapp.ui.screens.main.MainScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@ExperimentalMaterialApi
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit = {},
    showBottomSheet: () -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onNav = navController::navigate,
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                showBottomSheet = showBottomSheet,
                bottomSheetScaffoldState = bottomSheetScaffoldState
            )
        }
        composable(
            route = "${Screen.Detail.route}/{order}",
            arguments = listOf(navArgument("order") { type = NavType.StringType })
        ) { backStackEntry ->
            val order = backStackEntry.arguments?.getString("order") ?: ""
            DetailScreen(
                onNav = navController::navigate,
                onNavigateUp = navController::navigateUp,
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                openDrawer = openDrawer,
                order = Json.decodeFromString(order)
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Main: Screen("Main")
    object Detail: Screen("Detail")
}
