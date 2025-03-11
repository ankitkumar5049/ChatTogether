package com.example.chattogether.screens

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.R
import com.example.chattogether.utils.UserPreferences
import com.example.chattogether.viewmodel.AuthViewModel
import com.example.chattogether.navigation.Screen
import com.example.chattogether.utils.AppSession
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController?,
                 viewModel: AuthViewModel = viewModel(),
                 onLoginClick: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var loginText by remember { mutableStateOf("SignUp Screen") }
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
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            label = { Text("Name") }
        )

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
            value = phone,
            onValueChange = { phone = it },
            singleLine = true,
            label = { Text("Phone") }
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

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            singleLine = true,
            label = { Text("Confirm Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(modifier = Modifier.padding(10.dp))

        val buttonColor = if (isSystemInDarkTheme()) Color(0xFF448AFF) else Color(0xFF536DFE)
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            ,onClick = {
                AppSession.putString("email", email)
                authSetup(viewModel,
                name,
                email,
                phone,
                password,
                confirmPassword,
                context){
                    navController?.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true } // Clears SignUp from back stack
                    }
                }
                       },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor))
        {
            Text(text = "SignUp",)
        }


        Text(modifier = Modifier
            .align(Alignment.End)
            .padding(end = 15.dp)
            .clickable{
                onLoginClick()
            },
            text = "Already have an account! Login here",
            )

    }
}

fun authSetup(viewModel: AuthViewModel, name: String, email: String, phone: String, password: String,
              confirmPassword: String, context: Context, onSuccess: () -> Unit) {
    val userPreferences = UserPreferences(context)
    if (viewModel.checkValidation(name, phone, password, email, confirmPassword)) {
        viewModel.signUp(name, email, phone, password) { isSuccess, message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (isSuccess) {
                viewModel.viewModelScope.launch {
                    userPreferences.saveUserCredentials(email, password)
                    onSuccess() // Navigate to the main screen
                } // Navigate if sign-up is successful
            }
        }
    } else {
        Toast.makeText(context, "Please verify the details", Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    MaterialTheme {
        SignUpScreen(navController = null, onLoginClick = {})
    }
}
