package com.boww.crypto

import android.app.PendingIntent.getActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        NavigationUI.setupActionBarWithNavController(
            this,
            navHostFragment.navController,
//            appBarConfiguration
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        this.menu = menu
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_refresh_item -> {
                if (navHostFragment.navController.currentDestination?.id == R.id.listFragment) {
                    val list = navHostFragment.childFragmentManager.primaryNavigationFragment as ListFragment
                    list.refresh()
                }
                true
            }
            R.id.menu_settings_item -> {
                Log.i(TAG, navHostFragment.navController.currentDestination?.id.toString())
                Log.i(TAG, R.id.listFragment.toString())
                Log.i(TAG, R.id.startFragment.toString())
                if (navHostFragment.navController.currentDestination?.id == R.id.startFragment)
                    navHostFragment.navController.navigate(R.id.action_startFragment_to_mainSettingsFragment)
                if (navHostFragment.navController.currentDestination?.id == R.id.listFragment)
                    navHostFragment.navController.navigate(R.id.action_listFragment_to_mainSettingsFragment2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}