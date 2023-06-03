package com.temtem.interactive.map.temzone.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.temtem.interactive.map.temzone.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Set the display content edge to edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Navigate to map fragment if user is already signed in
        if (viewModel.isUserSignedIn()) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
            val navController = navHostFragment.navController
            val navInflater = navController.navInflater
            val graph = navInflater.inflate(R.navigation.graph_navigation)

            graph.setStartDestination(R.id.map_fragment)

            navController.setGraph(graph, intent.extras)
        }
    }
}
