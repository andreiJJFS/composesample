package com.jjfs.android.composetestapp.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.ui.MainActivity
import com.jjfs.android.composetestapp.ui.MainApp
import com.jjfs.android.composetestapp.ui.Screen
import com.jjfs.android.composetestapp.ui.screens.main.*
import com.jjfs.android.composetestapp.ui.theme.ComposeTestAppTheme
import com.jjfs.android.composetestapp.ui.utils.Event
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val vm: MainViewModel = mockk(relaxed = true)
    val openLogin: () -> Unit = mockk(relaxed = true)
    private val onNav: (String) -> Unit = mockk(relaxed = true)

    private val state = MutableStateFlow(MainViewModelState())
    private val eventChannel = MutableSharedFlow<Event>()

    @Before
    fun setup() {
        every { vm.stateFlow } returns state
        every { vm.eventChannel } returns eventChannel.asSharedFlow()
        composeTestRule.setContent {
            ComposeTestAppTheme {
                MainScreen(
                    viewModel = vm,
                    onNav = onNav,
                    showBottomSheet = openLogin
                )
            }
        }
    }

    @Test
    fun loadingSpinnerIsShown() {
        state.value = MainViewModelState(isLoading = true)
        composeTestRule.onNode(
            hasProgressBarRangeInfo(
                ProgressBarRangeInfo(
                    current= 0.0F,
                    range= 0.0F..0.0F,
                    steps= 0
                )
            )
        ).assertIsDisplayed()
    }

    @Test
    fun showOrders() {
        state.value = MainViewModelState(orders = ordersList)
        composeTestRule.onNode(hasScrollAction()).onChildren().assertCountEquals(ordersList.size)
    }

    @ExperimentalTestApi
    @Test
    fun swipeOrderLeft() {
        val index = 2
        state.value = MainViewModelState(orders = ordersList)
        composeTestRule.onNode(hasScrollAction()).onChildren().run {
            assertCountEquals(ordersList.size)
            onChildAt(index).performGesture { swipeLeft(0F, -900F) }
        }
        verify(exactly = 1) { vm.swipeLeft(ordersList[index].orderId) }
    }

    @ExperimentalTestApi
    @Test
    fun swipeOrderRight() {
        state.value = MainViewModelState(orders = ordersList)
        composeTestRule.onNode(hasScrollAction()).onChildren().run {
            assertCountEquals(ordersList.size)
            onFirst().performGesture { swipeRight(0F, 900F) }
        }
        verify(exactly = 1) { vm.swipeRight(ordersList[0].orderId) }
    }

    @Test
    fun onError() {
        val error = "There was an error"
        state.value = MainViewModelState(error = error)
        composeTestRule.onNodeWithText(error).assertExists()
    }

    @Test
    fun eventChannelOpensLogin() {
        runBlocking {
            eventChannel.emit(Event.OpenLogin() {})
        }
        verify(exactly = 1) { openLogin() }
    }

    @Test
    fun testNavigates() {
        runBlocking {
            eventChannel.emit(Event.Navigate<Any>(Screen.Detail) )
        }
        verify(exactly = 1) { onNav(Screen.Detail.route) }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainScreenComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalTestApi
    @Test
    fun testOrderListSwipeLeft() {
        composeTestRule.setContent {
            OrderList(
               orderList = ordersList.map { OrderItem(it) },
                swipeLeft = {},
                swipeRight = {}
            )
        }
        composeTestRule.onNode(hasScrollAction()).onChildren().run {
            assertCountEquals(ordersList.size)
            onChildAt(2).performGesture { swipeLeft(0F, -600F) }
        }
        composeTestRule.onNodeWithContentDescription("Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText(ordersList[2].orderId).assertIsNotDisplayed()
    }

    @Test
    fun testOrderCardIsDisplayed() {
        composeTestRule.setContent {
            OrderCard(
                order = OrderItem(ordersList[0]),
                direction = DismissDirection.EndToStart
            )
        }
        composeTestRule.onNodeWithText("ID1234").assertExists()
    }
}

val ordersList = listOf(
    Order(
        orderId = "ID1234",
        supplierName = "Supplier One"
    ),
    Order(
        orderId = "ID4356",
        supplierName = "Supplier Two"
    ),
    Order(
        orderId = "ID6523",
        supplierName = "Supplier Three"
    ),
    Order(
        orderId = "ID7456",
        supplierName = "Supplier Four"
    )
)

fun SemanticsNodeInteractionCollection.onChildAt(position: Int): SemanticsNodeInteraction {
    return get(position)
}

/**
 * Launches the app from a test context
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
fun ComposeContentTestRule.launchApp() {
    setContent {
        MainApp()
    }
}