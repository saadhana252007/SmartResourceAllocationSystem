package com.example.smartresourceallocation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.ActivityRegisterBinding
import com.example.smartresourceallocation.viewmodel.AuthViewModel
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.ui.admin.AdminHomeActivity
import com.example.smartresourceallocation.ui.dashboard.UserDashboardActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    private var selectedRole = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityRegisterBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this)[
                AuthViewModel::class.java
            ]

        binding.rbAdmin.setOnClickListener {
            selectedRole = "ADMIN"
        }

        binding.rbUser.setOnClickListener {
            selectedRole = "USER"
        }

        binding.btnRegister.setOnClickListener {

            val name =
                binding.etName.text
                    .toString()
                    .trim()

            val email =
                binding.etEmail.text
                    .toString()
                    .trim()

            val password =
                binding.etPassword.text
                    .toString()
                    .trim()

            if (name.isEmpty()) {

                binding.etName.error = "Enter Name"

                return@setOnClickListener

            }

            if (name.length < 3) {

                binding.etName.error = "Minimum 3 characters"

                return@setOnClickListener

            }

            if (email.isEmpty()) {

                binding.etEmail.error = "Enter Email"

                return@setOnClickListener

            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.etEmail.error = "Invalid Email"

                return@setOnClickListener

            }

            if (password.isEmpty()) {

                binding.etPassword.error = "Enter Password"

                return@setOnClickListener

            }

            if (password.length < 6) {

                binding.etPassword.error =
                    "Password must be at least 6 characters"

                return@setOnClickListener

            }

            viewModel.register(
                name,
                email,
                password,
                selectedRole
            )
        }

        viewModel.registerResponse.observe(this) {

            Toast.makeText(
                this,
                it.message,
                Toast.LENGTH_SHORT
            ).show()

            val pref = SharedPrefManager(this)

            pref.saveToken(it.token)
            pref.saveRole(it.role)
            pref.saveUserName(it.name)
            pref.saveEmail(it.email)
            pref.saveCreatedAt(it.createdAt)

            if (it.role == "ADMIN") {

                val intent = Intent(
                    this,
                    AdminHomeActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                finish()

            } else {

                val intent = Intent(
                    this,
                    UserDashboardActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                finish()

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

        binding.tvLogin.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }
    }
}