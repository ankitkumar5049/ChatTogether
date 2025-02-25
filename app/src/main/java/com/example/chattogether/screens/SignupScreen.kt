package com.example.grocio.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chattogether.viewmodel.AuthViewModel

@Composable
fun SignupScreen(viewModel: AuthViewModel = viewModel()) {

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


        Text(modifier = Modifier
            .padding(5.dp),
            fontStyle = FontStyle.Normal,
            style = TextStyle.Default,
            maxLines = 1,
            fontSize = 30.sp,
            text = loginText)

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            ,onClick = {
                authSetup(viewModel,
                name,
                email,
                phone,
                password,
                confirmPassword,
                context)
                       },
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray))
        {
            Text(text = "SignUp",
                color = Color.Black)
        }


        Text(modifier = Modifier
            .align(Alignment.End)
            .padding(end = 15.dp),
            text = "Already have an account! Login here",
            color = Color.Blue)

    }
}

fun authSetup(viewModel: AuthViewModel, name: String, email: String, phone: String, password: String, confirmPassword: String, context: Context) {
    if (viewModel.checkValidation(name, phone, password, email, confirmPassword)) {
        viewModel.signUp(name, email, phone, password) { isSuccess, message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Please verify the details", Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    MaterialTheme {
        SignupScreen()
    }
}
