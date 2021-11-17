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
import com.jjfs.android.composetestapp.ui.screens.graphql.GraphqlDetailScreen
import com.jjfs.android.composetestapp.ui.screens.graphql.GraphqlScreen
import com.jjfs.android.composetestapp.ui.screens.graphql.GraphqlViewModel
import com.jjfs.android.composetestapp.ui.screens.main.MainScreen
import com.jjfs.android.composetestapp.ui.screens.main.MainViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialApi
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit = {},
    showBottomSheet: () -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    /** for sharing the viewModel */
    val graphqlViewModel = getViewModel<GraphqlViewModel>()

    NavHost(
        navController = navController,
        startDestination = Screen.Graphql.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Main.route) {
            val vm = getViewModel<MainViewModel>()
            MainScreen(
                onNav = navController::navigate,
                viewModel = vm,
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
                scaffoldState = scaffoldState,
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                openDrawer = openDrawer,
                order = Json.decodeFromString(order)
            )
        }
        composable(route = Screen.Graphql.route) {
            GraphqlScreen(
                onNav = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                viewModel = graphqlViewModel,
                showBottomSheet = showBottomSheet,
                bottomSheetScaffoldState = bottomSheetScaffoldState
            )
        }
        composable(
            route = "${Screen.GraphqlDetail.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            GraphqlDetailScreen(
                onNav = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                viewModel = graphqlViewModel,
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                id = id
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Main: Screen("Main")
    object Detail: Screen("Detail")
    object Graphql: Screen("Graphql")
    object GraphqlDetail: Screen("GraphqlDetail")
}
