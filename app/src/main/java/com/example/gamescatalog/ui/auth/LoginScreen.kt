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
fun LoginScreen(
    // Tambahkan parameter untuk navigasi ke halaman Register dan Home
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    // Mendapatkan instance AuthViewModel menggunakan factory yang telah dibuat.
    val viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )
    // Mengobservasi state dari ViewModel
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // LaunchedEffect untuk menangani side-effect seperti navigasi atau menampilkan Toast.
    LaunchedEffect(key1 = uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                navigateToHome() // Navigasi ke halaman utama
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState() // Reset state setelah menampilkan error
            }
            else -> Unit // Abaikan state lain seperti Idle atau Loading
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Text field untuk email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading // Nonaktifkan saat loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Text field untuk password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = uiState !is AuthUiState.Loading // Nonaktifkan saat loading
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tampilkan CircularProgressIndicator jika state sedang Loading
        if (uiState is AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            // Tombol Login
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol untuk ke halaman Register
        TextButton(onClick = navigateToRegister) {
            Text("Belum punya akun? Daftar di sini")
        }
    }
}