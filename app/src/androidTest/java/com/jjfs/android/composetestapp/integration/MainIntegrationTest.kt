package com.jjfs.android.composetestapp.integration

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.jjfs.android.composetestapp.ui.MainActivity
import com.jjfs.android.composetestapp.ui.MainApp
import com.jjfs.android.composetestapp.ui.screens.ordersList
import com.jjfs.android.composetestapp.ui.theme.ComposeTestAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterialApi
@ExperimentalTestApi
class MainIntegrationTest {

    @get:Rule
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            ComposeTestAppTheme {
                MainApp()
            }
        }
    }

    @Test
    fun mainIntegrationTest() {
        performLogin()
        testMainScreen()
        testDetailScreen()
        composeTestRule.onNode(hasScrollAction()).onChildren()
            .assertCountEquals(ordersList.size)
    }

    private fun performLogin() {
        composeTestRule.onNodeWithTag("pinInput").performTextInput("1234")
        composeTestRule.onNodeWithText("Submit").performClick()
    }

    private fun testMainScreen() {
        composeTestRule.onRoot().printToLog("tag")
        composeTestRule.waitForIdle()
        composeTestRule.onNode(
            hasProgressBarRangeInfo(
                ProgressBarRangeInfo(
                    current= 0.0F,
                    range= 0.0F..0.0F,
                    steps= 0
                )
            )
        ).assertIsDisplayed()

        composeTestRule.waitUntil(10_000L) {
            composeTestRule.onAllNodesWithContentDescription("list")
                .fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNode(hasScrollAction()).onChildren()
            .assertCountEquals(ordersList.size)
            .onFirst()
            .performGesture { swipeRight(0F, 900F) }
    }



    private fun testDetailScreen() {
        val description = "New description"
        composeTestRule.onNodeWithText(ordersList[0].orderId).assertIsDisplayed()
        composeTestRule.onNodeWithText(ordersList[0].supplierName).assertIsDisplayed()
        composeTestRule.onNodeWithTag("descriptionInput").performTextInput(description)
        composeTestRule.onNodeWithText(description).assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").performClick()

    }
}