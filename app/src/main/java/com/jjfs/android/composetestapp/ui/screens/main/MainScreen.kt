package com.jjfs.android.composetestapp.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jjfs.android.composetestapp.ui.components.BaseScaffold
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.androidx.compose.getViewModel

@ExperimentalMaterialApi
@Composable
fun MainScreen(
    onNav: (String) -> Unit = {},
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    viewModel: MainViewModel = getViewModel(),
    showBottomSheet: () -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
) {
    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventChannel.collectLatest { event ->
            when (event) {
                is Event.Navigate<*> -> onNav("${event.screen.route}/${event.args}")
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

    BaseScaffold(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        openDrawer = openDrawer,
        onDismissCallback = { viewModel.getOrders() },
        showBottomSheet = showBottomSheet
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.isLoading),
            onRefresh = { viewModel.getOrders() },
            content = {
                MainScreenContent(
                    state = state,
                    swipeLeft = { id: String -> viewModel.swipeLeft(id) },
                    swipeRight = { id: String ->  viewModel.swipeRight(id) }
                )
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun MainScreenContent(
    state: MainViewModelState,
    swipeRight: (String) -> Unit,
    swipeLeft: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        OrderList(
            orderList = state.orders.map { OrderItem(it) },
            swipeLeft = swipeLeft,
            swipeRight = swipeRight
        )
        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@ExperimentalMaterialApi
@Composable
fun OrderList(
    orderList: List<OrderItem>,
    swipeLeft: (String) -> Unit,
    swipeRight: (String) -> Unit,
) {
    Box {
        LazyColumn {
            items(
                items = orderList,
                key = { order -> order.orderId },

            ) { order ->
                SwipeableOrder(
                    order = order,
                    onSwipeRight = swipeLeft,
                    onSwipeLeft = swipeRight
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SwipeableOrder(
    order: OrderItem,
    onSwipeRight: (String) -> Unit,
    onSwipeLeft: (String) -> Unit,
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { direction ->
            when (direction) {
                DismissValue.DismissedToEnd -> onSwipeLeft(order.orderId)
                DismissValue.DismissedToStart ->  onSwipeRight(order.orderId)
                DismissValue.Default -> Unit
            }
            direction != DismissValue.DismissedToEnd
//                    || direction != DismissValue.DismissedToStart
        }
    )
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.6F) },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color: Color by animateColorAsState(
                when (direction) {
                    DismissDirection.StartToEnd -> MaterialTheme.colors.secondaryVariant
                    DismissDirection.EndToStart -> MaterialTheme.colors.error
                    else -> Color.LightGray
                }
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Done
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .scale(1f)
                        .padding(16.dp)
                )
            }
        },
        dismissContent = { OrderCard(order = order, direction = dismissState.dismissDirection) }
    )
}

@Composable
fun OrderCard(
    order: OrderItem,
    direction: DismissDirection?,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth().clickable {  },
        elevation = animateDpAsState(
            if (direction != null) 8.dp else 0.dp
        ).value
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = order.orderId, modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically))
            Text(text = order.supplierName, modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically))
        }
    }
}
