package com.example.smartresourceallocation.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityUserHomeBinding
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.ui.resource.ResourceActivity

class UserHomeActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityUserHomeBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityUserHomeBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        binding.btnLogout.setOnClickListener {

            val pref =
                SharedPrefManager(this)

            pref.clearToken()
            pref.clearRole()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()

        }
        startActivity(
            Intent(
                this,
                ResourceActivity::class.java
            )
        )

    }

}