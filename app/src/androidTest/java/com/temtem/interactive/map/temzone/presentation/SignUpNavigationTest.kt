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
import com.temtem.interactive.map.temzone.presentation.auth.sign_up.SignUpFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class SignUpNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navigateToSignInFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<SignUpFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.sign_up_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.sign_in_text_view)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.sign_in_fragment)
    }

    @Test
    fun navigateToPrivacyPolicyPdfFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<SignUpFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.sign_up_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.privacy_policy_text_view)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.pdf_fragment)
    }

    @Test
    fun navigateToTermsOfServicePdfFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<SignUpFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.sign_up_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.terms_of_service_text_view)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.pdf_fragment)
    }
}
