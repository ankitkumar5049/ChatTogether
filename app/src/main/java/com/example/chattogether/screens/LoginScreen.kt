package com.example.chattogether.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.R
import com.example.chattogether.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController?,
                viewModel: AuthViewModel = viewModel(),
                onLoginSuccess: () -> Unit,
                onSignUpClick: () -> Unit) {


    var email by remember { mutableStateOf("") }
    var loginText by remember { mutableStateOf("Login Screen") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {


            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape) // Makes it round
                    .border(2.dp, Color.White, CircleShape), // Optional border
                contentScale = ContentScale.Crop // Ensures image fills the circle
            )

        Spacer(modifier = Modifier.padding(25.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("Email") }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            value = password,
            singleLine = true,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(modifier = Modifier.padding(10.dp))

        val buttonColor = if (isSystemInDarkTheme()) Color(0xFF448AFF) else Color(0xFF536DFE)
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            ,onClick = {
                viewModel.login(email, password) { success ->
                    if (success) {
                        onLoginSuccess()  // Navigate to Dashboard
                    } else {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor))
        {
            Text(text = "Login",
                )
        }


        Text(
            text = "SignUp here!",
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 15.dp)
                .clickable {
                    onSignUpClick()
                }
        )

    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        LoginScreen(navController = null,onLoginSuccess = {}, onSignUpClick = {})
    }
}
