package com.example.gamescatalog.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamescatalog.ui.ViewModelFactory

@Composable
fun RegisterScreen(
    // Parameter untuk navigasi kembali ke halaman Login
    navigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    // Menggunakan AuthViewModel dan Factory yang sama dengan LoginScreen
    val viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Membuat state untuk setiap input field
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // LaunchedEffect untuk menangani side-effect seperti notifikasi dan navigasi
    LaunchedEffect(key1 = uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                // Tampilkan pesan sukses dan arahkan kembali ke halaman login
                Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState() // Reset state agar pesan error tidak muncul terus
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Text field untuk Nama
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Text field untuk Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Text field untuk Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = uiState !is AuthUiState.Loading
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Menampilkan Indikator Loading atau Tombol Register
        if (uiState is AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.register(name, email, password) },
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text("Daftar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = navigateToLogin) {
            Text("Sudah punya akun? Masuk di sini")
        }
    }
}