package com.caretail.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.caretail.data.Role
import com.caretail.viewmodel.AuthState
import com.caretail.viewmodel.AuthViewModel

private val AuthState.Success.user: Any
    get() {
        TODO()
    }

@Composable
fun LoginScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val user = (authState as AuthState.Success).user
            authViewModel.resetAuthState()

            val destination = if (user.getRoleEnum() == Role.OWNER) {
                "owner_home"
            } else {
                "sitter_home"
            }

            nav.navigate(destination) {
                popUpTo("landing") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = { nav.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "Welcome Back 🐾",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                enabled = authState !is AuthState.Loading,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (authState is AuthState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = { showForgotPassword = true },
                enabled = authState !is AuthState.Loading
            ) {
                Text("Forgot Password?")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.login(email.trim(), password)
                    }
                },
                enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { nav.navigate("register") }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }

    if (showForgotPassword) {
        ForgotPasswordDialog(
            authViewModel = authViewModel,
            onDismiss = { showForgotPassword = false }
        )
    }
}

private fun Any.getRoleEnum() {
    TODO("Not yet implemented")
}


@Composable
fun RegisterScreen(
    nav: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(Role.OWNER) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Additional fields for sitters
    var workHours by remember { mutableStateOf("") }
    var expertise by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetAuthState()

            val destination = if (selectedRole == Role.OWNER) {
                "owner_home"
            } else {
                "sitter_home"
            }

            nav.navigate(destination) {
                popUpTo("landing") { inclusive = true }
            }
        }
    }

    fun validateInputs(): String? {
        return when {
            name.isBlank() -> "Please enter your name"
            email.isBlank() -> "Please enter your email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address"
            password.isBlank() -> "Please enter a password"
            password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword != password -> "Passwords do not match"
            phone.isBlank() -> "Please enter your phone number"
            dob.isBlank() -> "Please enter your date of birth"
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = { nav.popBackStack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Create Your Account 🐾", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Role selection
            Text("I am a:", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRole == Role.OWNER,
                    onClick = { selectedRole = Role.OWNER },
                    label = { Text("Pet Owner") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedRole == Role.SITTER,
                    onClick = { selectedRole = Role.SITTER },
                    label = { Text("Pet Sitter") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Common fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; validationError = null },
                label = { Text("Full Name") },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; validationError = null },
                label = { Text("Email Address") },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; validationError = null },
                label = { Text("Password") },
                enabled = authState !is AuthState.Loading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; validationError = null },
                label = { Text("Confirm Password") },
                enabled = authState !is AuthState.Loading,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; validationError = null },
                label = { Text("Phone Number") },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it; validationError = null },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                enabled = authState !is AuthState.Loading,
                placeholder = { Text("01/01/1990") },
                modifier = Modifier.fillMaxWidth()
            )

            // Additional fields for sitters
            if (selectedRole == Role.SITTER) {
                OutlinedTextField(
                    value = workHours,
                    onValueChange = { workHours = it },
                    label = { Text("Work Hours (Optional)") },
                    enabled = authState !is AuthState.Loading,
                    placeholder = { Text("e.g., 9am-5pm or Flexible") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expertise,
                    onValueChange = { expertise = it },
                    label = { Text("Expertise (Optional)") },
                    enabled = authState !is AuthState.Loading,
                    placeholder = { Text("e.g., Dogs, Cats, Birds") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    enabled = authState !is AuthState.Loading,
                    placeholder = { Text("City or area") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address (Optional)") },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            // Error messages
            val errorMessage = validationError ?: (if (authState is AuthState.Error) (authState as AuthState.Error).message else null)

            if (errorMessage != null) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            // Register button
            Button(
                onClick = {
                    validationError = validateInputs()
                    if (validationError == null) {
                        authViewModel.register(
                            email = email.trim(),
                            password = password,
                            name = name.trim(),
                            phone = phone.trim(),
                            dob = dob.trim(),
                            role = selectedRole,
                            address = address.trim(),
                            workHours = workHours.trim(),
                            expertise = expertise.trim(),
                            location = location.trim()
                        )
                    }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign Up")
                }
            }

            TextButton(onClick = { nav.navigate("login") }) {
                Text("Already have an account? Login")
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column {
                if (message == null) {
                    Text("Enter your email address and we'll send you a password reset link.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        message!!,
                        color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            if (message == null) {
                TextButton(
                    onClick = {
                        if (email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            authViewModel.sendPasswordReset(email.trim()) { success, msg ->
                                isSuccess = success
                                message = msg
                            }
                        } else {
                            isSuccess = false
                            message = "Please enter a valid email address"
                        }
                    }
                ) {
                    Text("Send Reset Link")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (message == null) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

private fun AuthViewModel.sendPasswordReset(email: String, function: Any) {}
