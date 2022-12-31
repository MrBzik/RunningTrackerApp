package com.example.runningtrackerapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.utils.Constants.INTENT_TO_TRACKING_FRAG
import com.example.runningtrackerapp.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var navController : NavController

    @Inject
    lateinit var userName : String

    lateinit var toolbarText : MaterialTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

         toolbarText = findViewById(R.id.tvToolbarTitle)

        toolbarText.text = "Let's go, $userName!"

        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
         navController = navHostFragment.navController

        checkPendingIntentForTrackingFragment(intent)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavView.setupWithNavController(navController)

        bottomNavView.setOnItemReselectedListener { /*DO NOTHING*/}

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when(destination.id){
                        R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment
                            -> bottomNavView.visibility = View.VISIBLE
                        else -> bottomNavView.visibility = View.GONE
                    }
                }
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        checkPendingIntentForTrackingFragment(intent)
    }

    private fun checkPendingIntentForTrackingFragment(intent: Intent?) {
        if(intent?.action == INTENT_TO_TRACKING_FRAG) {

            navController.navigate(R.id.action_global_to_trackingFragment)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
           this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions(this)
        }
    }

    fun requestPermissions(context: Context){
        if(Utils.hasLocationPermissions(context)){
            return
        }
            EasyPermissions.requestPermissions(this,
                "App needs your location to work", 1,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestBackGroundPermission(context: Context){
        if(Utils.hasLocationPermissions(context)){
            return
        }
        EasyPermissions.requestPermissions(this,
            "App needs your location to work", 2,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }


}