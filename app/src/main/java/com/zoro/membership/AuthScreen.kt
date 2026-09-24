package com.zoro.membership

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(context: Context, onAuthenticated: (AuthClient.AuthUser) -> Unit) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Zoro", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            if (isSignUp) "Create your account" else "Welcome back",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                errorMessage = null
                isLoading = true
                scope.launch {
                    try {
                        val user = if (isSignUp) {
                            AuthClient.signUp(context, email.trim(), password)
                        } else {
                            AuthClient.signIn(context, email.trim(), password)
                        }
                        onAuthenticated(user)
                    } catch (e: Exception) {
                        errorMessage = e.message
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && email.isNotBlank() && password.length >= 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text(if (isSignUp) "Sign Up" else "Log In")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { isSignUp = !isSignUp; errorMessage = null }) {
            Text(if (isSignUp) "Already have an account? Log in" else "New here? Create an account")
        }
    }
}
