package com.example.smartresourceallocation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.databinding.ActivityRegisterBinding
import com.example.smartresourceallocation.viewmodel.AuthViewModel

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

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

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