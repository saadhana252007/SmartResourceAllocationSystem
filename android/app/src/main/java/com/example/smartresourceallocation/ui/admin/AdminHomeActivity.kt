package com.example.smartresourceallocation.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ActivityAdminHomeBinding
import com.example.smartresourceallocation.ui.admin.fragments.analytics.AnalyticsFragment
import com.example.smartresourceallocation.ui.admin.fragments.profile.ProfileFragment
import com.example.smartresourceallocation.ui.admin.fragments.reservations.ReservationsFragment
import com.example.smartresourceallocation.ui.admin.fragments.resources.ResourcesFragment

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.adminBottomNavigation.selectedItemId = R.id.nav_analytics
        }
    }

    private fun setupBottomNavigation() {

        binding.adminBottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_resources -> {
                    loadFragment(ResourcesFragment())
                    true
                }

                R.id.nav_reservations -> {
                    loadFragment(ReservationsFragment())
                    true
                }

                R.id.nav_analytics -> {
                    loadFragment(AnalyticsFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .commit()
    }
}