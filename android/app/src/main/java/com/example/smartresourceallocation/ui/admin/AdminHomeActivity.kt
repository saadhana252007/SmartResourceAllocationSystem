package com.example.smartresourceallocation.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityAdminHomeBinding
import com.example.smartresourceallocation.ui.auth.LoginActivity
import com.example.smartresourceallocation.utils.SharedPrefManager

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityAdminHomeBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityAdminHomeBinding.inflate(
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

    }

}