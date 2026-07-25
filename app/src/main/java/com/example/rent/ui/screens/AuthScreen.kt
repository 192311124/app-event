package com.example.rent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rent.data.repository.AuthRepository
import com.example.rent.ui.components.GlassCard
import com.example.rent.ui.theme.*
import com.example.rent.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    initialMode: String = "signin",
    viewModel: AuthViewModel,
    onNavigate: (String, Map<String, Any>) -> Unit
) {
    var mode by remember { mutableStateOf(initialMode) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val toastMsg by viewModel.toastMessage.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var usernameError by remember { mutableStateOf<String?>(null) }
    var savePasswordOption by remember { mutableStateOf(true) }
    var showSavePrompt by remember { mutableStateOf(false) }

    var savedUser by remember { mutableStateOf<String?>(null) }
    var savedPass by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
        savedUser = prefs.getString("saved_username", null)
        savedPass = prefs.getString("saved_password", null)
        if (!savedUser.isNullOrEmpty() && username.isEmpty()) {
            username = savedUser ?: ""
        }
        if (!savedPass.isNullOrEmpty() && password.isEmpty()) {
            password = savedPass ?: ""
        }
    }

    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            val sanitized = msg
                .replace("email address", "username", ignoreCase = true)
                .replace("email", "username", ignoreCase = true)
            if (sanitized.contains("already exists", ignoreCase = true) || sanitized.contains("in use", ignoreCase = true) || sanitized.contains("duplicate", ignoreCase = true)) {
                usernameError = "Username already exists. Please sign in instead."
            }
            android.widget.Toast.makeText(context, sanitized, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearToast()
        }
    }

    val (passLen, passUpper, passNum, passSpec) = remember(password) {
        val (isValid, _) = AuthRepository.validatePassword(password)
        Tuple4(
            password.length >= 6,
            password.contains(Regex("[A-Z]")),
            password.contains(Regex("[0-9]")),
            password.contains(Regex("[!@#$%^&*(),.?\":{}|<>]"))
        )
    }

    if (showSavePrompt) {
        AlertDialog(
            onDismissRequest = {
                showSavePrompt = false
                onNavigate("location", emptyMap())
            },
            containerColor = BgSecondary,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔒", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Password?", color = PrimaryGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    text = "Would you like to save the password for \"$username\" to sign in easily next time?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSavePrompt = false
                        val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("saved_username", username)
                            .putString("saved_password", password)
                            .apply()
                        viewModel.showToast("Password saved successfully!")
                        onNavigate("location", emptyMap())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSavePrompt = false
                        val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()
                        viewModel.showToast("Password not saved.")
                        onNavigate("location", emptyMap())
                    }
                ) {
                    Text("Don't Save", color = ErrorRed)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate("back", emptyMap()) }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Back", tint = PrimaryGold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (mode == "signup") "Create Account" else "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGold
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (mode == "signup")
                "Register to book event decorations & track live orders"
            else
                "Sign in to access your orders, cart & saved wishlist",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (mode == "signin" && !savedUser.isNullOrEmpty()) {
            Surface(
                color = PrimaryGold.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Saved Password: $savedUser",
                        fontSize = 12.sp,
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Don't Save",
                        fontSize = 11.sp,
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().clear().apply()
                            savedUser = null
                            savedPass = null
                            username = ""
                            password = ""
                        }
                    )
                }
            }
        }

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                    focusedContainerColor = BgSecondary,
                    unfocusedContainerColor = BgSecondary,
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = Color.White,
                    selectionColors = androidx.compose.foundation.text.selection.TextSelectionColors(
                        handleColor = PrimaryGold,
                        backgroundColor = PrimaryGold.copy(alpha = 0.4f)
                    )
                )

                val errorTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = BgSecondary,
                    unfocusedContainerColor = BgSecondary,
                    focusedBorderColor = ErrorRed,
                    unfocusedBorderColor = ErrorRed,
                    errorBorderColor = ErrorRed,
                    focusedLabelColor = ErrorRed,
                    unfocusedLabelColor = ErrorRed,
                    cursorColor = ErrorRed
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { 
                        username = it
                        usernameError = null
                    },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGold) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = if (usernameError != null) errorTextFieldColors else textFieldColors,
                    isError = usernameError != null,
                    singleLine = true
                )
                if (usernameError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ $usernameError",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = textFieldColors,
                    singleLine = true
                )

                if (mode == "signup") {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Checklist Card
                    Surface(
                        color = Color(0x66000000),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Password Criteria:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            CheckRow("At least 6 characters long", passLen)
                            CheckRow("At least 1 uppercase letter (A-Z)", passUpper)
                            CheckRow("At least 1 number (0-9)", passNum)
                            CheckRow("At least 1 special character (!@#\$%)", passSpec)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { savePasswordOption = !savePasswordOption }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = savePasswordOption,
                        onCheckedChange = { savePasswordOption = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryGold,
                            uncheckedColor = TextMuted,
                            checkmarkColor = BgPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save Password for easy sign in",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (mode == "signup") {
                            if (password != confirmPassword) {
                                viewModel.showToast("Passwords do not match.")
                                return@Button
                            }
                            viewModel.signUp(username, password) {
                                if (savePasswordOption) {
                                    showSavePrompt = true
                                } else {
                                    val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
                                    prefs.edit().clear().apply()
                                    onNavigate("location", emptyMap())
                                }
                            }
                        } else {
                            viewModel.signIn(username, password) {
                                val prefs = context.getSharedPreferences("rent_auth_prefs", android.content.Context.MODE_PRIVATE)
                                if (savePasswordOption) {
                                    prefs.edit()
                                        .putString("saved_username", username)
                                        .putString("saved_password", password)
                                        .apply()
                                } else {
                                    prefs.edit().clear().apply()
                                }
                                onNavigate("location", emptyMap())
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BgPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = BgPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (mode == "signup") "Create Account" else "Sign In Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                text = if (mode == "signup") "Already have an account? " else "Don't have an account? ",
                color = TextSecondary
            )
            Text(
                text = if (mode == "signup") "Sign In" else "Sign Up",
                color = PrimaryGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    mode = if (mode == "signup") "signin" else "signup"
                }
            )
        }
    }
}

@Composable
fun CheckRow(text: String, isPassed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isPassed) SuccessGreen else TextMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isPassed) SuccessGreen else TextMuted
        )
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
