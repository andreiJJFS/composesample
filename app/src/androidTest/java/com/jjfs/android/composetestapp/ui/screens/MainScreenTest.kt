package com.jjfs.android.composetestapp.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.ui.Greeting
import com.jjfs.android.composetestapp.ui.MainActivity
import com.jjfs.android.composetestapp.ui.MainApp
import com.jjfs.android.composetestapp.ui.screens.auth.LoginButton
import com.jjfs.android.composetestapp.ui.screens.main.*
import com.jjfs.android.composetestapp.ui.theme.ComposeTestAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainScreenTest {

    @get:Rule
//    val composeTestRule = createComposeRule()
    val composeTestRule = createAndroidComposeRule<MainActivity>()


//    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun mainTest() {
        composeTestRule.setContent {
            val scaffoldState = rememberScaffoldState()
            val loginScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
            )

//            MainScreen(
//                scaffoldState = scaffoldState,
//                openDrawer = { /*TODO*/ },
//                showBottomSheet = { /*TODO*/ },
//                bottomSheetScaffoldState = loginScaffoldState
//            )
            OrderCard(
                order = OrderItem(ordersList[0]),
                direction = DismissDirection.EndToStart
            )
        }
        composeTestRule.onNodeWithText("ID1234").assertExists()
    }

    @Test
    fun testLogin() {
        composeTestRule.setContent {
            Greeting(name = "andrei")
//            LoginButton(
//                onClick = {},
//                isLoading = true
//            )
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
    ),
)

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