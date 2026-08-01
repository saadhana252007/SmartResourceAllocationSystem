package com.example.smartresourceallocation.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.ui.admin.AdminHomeActivity
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.ui.dashboard.UserDashboardActivity
import com.example.smartresourceallocation.utils.SharedPrefManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val pref = SharedPrefManager(this)

            val token = pref.getToken()

            val role = pref.getRole()

            if (!token.isNullOrBlank() && !role.isNullOrBlank())  {

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

        }, 2500)

    }

}