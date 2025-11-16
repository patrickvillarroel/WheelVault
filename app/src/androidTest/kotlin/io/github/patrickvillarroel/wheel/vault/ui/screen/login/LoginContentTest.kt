package io.github.patrickvillarroel.wheel.vault.ui.screen.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.patrickvillarroel.wheel.vault.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun loginContent_displaysAllButtons() {
        // When
        composeTestRule.setContent {
            LoginContent(
                onLoginWithEmailClick = {},
                onLoginWithEmailAndPasswordClick = {},
                onLoginWithGoogleClick = {},
                onRegisterClick = {},
            )
        }

        // Then
        composeTestRule.onNodeWithText(context.getString(R.string.register)).assertIsDisplayed()
    }

    @Test
    fun loginContent_registerButtonClickTriggersCallback() {
        // Given
        var registerClicked = false

        // When
        composeTestRule.setContent {
            LoginContent(
                onLoginWithEmailClick = {},
                onLoginWithEmailAndPasswordClick = {},
                onLoginWithGoogleClick = {},
                onRegisterClick = { registerClicked = true },
            )
        }

        // Then
        composeTestRule.onNodeWithText(context.getString(R.string.register))
            .performClick()

        assertTrue("Register button should trigger callback", registerClicked)
    }

    @Test
    fun loginContent_emailButtonClickTriggersCallback() {
        // Given
        var emailClicked = false

        // When
        composeTestRule.setContent {
            LoginContent(
                onLoginWithEmailClick = { emailClicked = true },
                onLoginWithEmailAndPasswordClick = {},
                onLoginWithGoogleClick = {},
                onRegisterClick = {},
            )
        }

        // Then - Find and click the email button
        val emailButtonText = context.getString(
            R.string.continue_with,
            context.getString(R.string.email),
        )
        composeTestRule.onNodeWithText(emailButtonText)
            .performClick()

        assertTrue("Email button should trigger callback", emailClicked)
    }

    @Test
    fun loginContent_emailPasswordButtonClickTriggersCallback() {
        // Given
        var emailPasswordClicked = false

        // When
        composeTestRule.setContent {
            LoginContent(
                onLoginWithEmailClick = {},
                onLoginWithEmailAndPasswordClick = { emailPasswordClicked = true },
                onLoginWithGoogleClick = {},
                onRegisterClick = {},
            )
        }

        // Then
        composeTestRule.onNodeWithText(context.getString(R.string.continue_with_email_password))
            .performClick()

        assertTrue("Email/Password button should trigger callback", emailPasswordClicked)
    }

    @Test
    fun loginContent_allButtonsAreClickable() {
        // Given
        var registerClicked = false
        var emailClicked = false
        var emailPasswordClicked = false
        var googleClicked = false

        // When
        composeTestRule.setContent {
            LoginContent(
                onLoginWithEmailClick = { emailClicked = true },
                onLoginWithEmailAndPasswordClick = { emailPasswordClicked = true },
                onLoginWithGoogleClick = { googleClicked = true },
                onRegisterClick = { registerClicked = true },
            )
        }

        // Then - Click all buttons
        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()

        val emailButtonText = context.getString(
            R.string.continue_with,
            context.getString(R.string.email),
        )
        composeTestRule.onNodeWithText(emailButtonText).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.continue_with_email_password))
            .performClick()

        assertTrue("All callbacks should be triggered", registerClicked && emailClicked && emailPasswordClicked)
    }
}
