package com.boww.crypto

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.elevation = 0f

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.navController,)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        this.menu = menu
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @ExperimentalTime
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_refresh_item -> {
                if (navHostFragment.navController.currentDestination?.id == R.id.dailyPriceListFragment) {
                    val list = navHostFragment.childFragmentManager.primaryNavigationFragment as DailyPriceListFragment
                    list.refresh()
                }
                else if (navHostFragment.navController.currentDestination?.id == R.id.dayOverviewFragment) {
                    val overview = navHostFragment.childFragmentManager.primaryNavigationFragment as DayOverviewFragment
                    overview.refresh()
                }
                true
            }
            R.id.menu_settings_item -> {
                if (navHostFragment.navController.currentDestination?.id == R.id.dailyPriceListFragment)
                    navHostFragment.navController.navigate(R.id.action_dailyPriceListFragment_to_mainSettingsFragment)
                if (navHostFragment.navController.currentDestination?.id == R.id.dayOverviewFragment)
                    navHostFragment.navController.navigate(R.id.action_dayOverviewFragment_to_mainSettingsFragment)
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