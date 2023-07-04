package com.temtem.interactive.map.temzone.presentation

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.launchFragmentInHiltContainer
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.SignInFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class SignInNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navigateToSignUpFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<SignInFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.sign_in_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.sign_up_text_view)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.sign_up_fragment)
    }

    @Test
    fun navigateToForgotPasswordFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<SignInFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.sign_in_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.forgot_password_text_view)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.forgot_password_fragment)
    }
}
