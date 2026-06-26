package com.example.smartresourceallocation.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.ui.home.HomeActivity
import com.example.smartresourceallocation.utils.SharedPrefManager

import com.example.smartresourceallocation.ui.admin.AdminHomeActivity
import com.example.smartresourceallocation.ui.user.UserHomeActivity
import com.example.smartresourceallocation.ui.dashboard.UserDashboardActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        val pref =
            SharedPrefManager(this)

        val token =
            pref.getToken()

        val role =
            pref.getRole()

        if (
            token != null &&
            role != null
        ) {

            if (role == "ADMIN") {

                startActivity(
                    Intent(
                        this,
                        AdminHomeActivity::class.java
                    )
                )

            } else {

                startActivity(
                    Intent(
                        this,
                        UserDashboardActivity::class.java
                    )
                )

            }

        } else {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

        }


        finish()

    }

}