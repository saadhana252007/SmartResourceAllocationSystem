package com.example.smartresourceallocation.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartresourceallocation.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_user_dashboard
        )

        val openReservationTab =
            intent.getBooleanExtra(
                "openReservationTab",
                false
            )

        val bottomNav =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigation
            )



        findViewById<BottomNavigationView>(
            R.id.bottomNavigation
        ).setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_resources -> {
                    loadFragment(ResourceFragment())
                    true
                }

                R.id.nav_reservations -> {
                    loadFragment(ReservationFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }

        }
        if(openReservationTab){

            bottomNav.selectedItemId =
                R.id.nav_reservations

        }else{

            bottomNav.selectedItemId =
                R.id.nav_home

        }

    }

    private fun loadFragment(
        fragment: Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()

    }

}