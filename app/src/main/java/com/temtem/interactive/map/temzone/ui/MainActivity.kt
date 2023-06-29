package com.temtem.interactive.map.temzone.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.presentation.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private companion object {
        private const val UPDATE_TYPE = AppUpdateType.IMMEDIATE
        private const val REQUEST_UPDATE = 2000
    }

    private val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Set the display content edge to edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Check for app updates
        checkAppUpdates()

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_UPDATE && resultCode != RESULT_OK) {
            checkAppUpdates()
        }
    }

    override fun onResume() {
        super.onResume()

        if (UPDATE_TYPE == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        it,
                        UPDATE_TYPE,
                        this,
                        REQUEST_UPDATE,
                    )
                }
            }
        }
    }

    private fun checkAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            val isUpdateAvailable = it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (UPDATE_TYPE) {
                AppUpdateType.FLEXIBLE -> it.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> it.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    UPDATE_TYPE,
                    this,
                    REQUEST_UPDATE,
                )
            }
        }
    }
}
