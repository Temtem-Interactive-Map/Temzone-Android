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
import com.temtem.interactive.map.temzone.presentation.auth.sign_up.SignUpFragment
import com.temtem.interactive.map.temzone.presentation.map.MapFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class MapNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navigateFromMapFragmentToSettingsFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<MapFragment> {
            navController.setGraph(R.navigation.graph_navigation)
            navController.setCurrentDestination(R.id.map_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.search_bar_menu_settings)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.settings_fragment)
    }
}
