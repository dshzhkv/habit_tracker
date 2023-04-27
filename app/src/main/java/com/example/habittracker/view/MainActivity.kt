package com.example.habittracker.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.habittracker.R
import com.example.habittracker.application.HabitTrackerApplication
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(){

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as HabitTrackerApplication).initViewModelComponent(this as ViewModelStoreOwner)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.nav_view)

        setupNavigation()

        setupAvatar()
    }

    private fun setupAvatar() {
        val header: View = navigationView.getHeaderView(0)
        val imageView: ImageView = header.findViewById(R.id.avatar)

        Glide.with(this)
            .load("https://memepedia.ru/wp-content/uploads/2019/09/mayk-vazovski-s-litsom-salli-8.jpg")
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(imageView)
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navigationView.setupWithNavController(navController)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.editHabitFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration)
}