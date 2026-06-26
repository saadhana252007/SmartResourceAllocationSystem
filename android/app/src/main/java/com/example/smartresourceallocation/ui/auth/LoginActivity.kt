package com.example.smartresourceallocation.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.ActivityLoginBinding
import com.example.smartresourceallocation.viewmodel.AuthViewModel
import android.content.Intent

import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.ui.home.HomeActivity
import com.example.smartresourceallocation.ui.admin.AdminHomeActivity
import com.example.smartresourceallocation.ui.user.UserHomeActivity
import com.example.smartresourceallocation.ui.dashboard.UserDashboardActivity
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding =
            ActivityLoginBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        viewModel =
            ViewModelProvider(this)[
                AuthViewModel::class.java
            ]

        binding.btnLogin.setOnClickListener {

            val email =
                binding.etEmail.text
                    .toString()
                    .trim()

            val password =
                binding.etPassword.text
                    .toString()
                    .trim()

            viewModel.login(
                email,
                password
            )

        }

        viewModel.loginResponse.observe(this) {

            Toast.makeText(
                this,
                it.name,
                Toast.LENGTH_LONG
            ).show()

            val pref =
                SharedPrefManager(this)

            pref.saveToken(
                it.token
            )

            pref.saveRole(
                it.role
            )

            pref.saveUserName(
                it.name
            )

            Toast.makeText(
                this,
                it.message,
                Toast.LENGTH_SHORT
            ).show()


            pref.saveToken(it.token)
            pref.saveRole(it.role)

            if (it.role == "ADMIN") {

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

            finish()

        }

        viewModel.errorMessage.observe(this) {

            Toast.makeText(
                this,
                it,
                Toast.LENGTH_SHORT
            ).show()

        }
        binding.tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )

        }

    }

}