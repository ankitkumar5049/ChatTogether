package com.example.chattogether.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.R
import com.example.chattogether.navigation.Screen
import com.example.chattogether.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun SignUpScreen(navController: NavController?,
                 viewModel: AuthViewModel = viewModel(),
                 onLoginClick: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisible1 by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                val today = Calendar.getInstance()
                val age = today.get(Calendar.YEAR) - selectedCalendar.get(Calendar.YEAR)

                // Check if user is 16 or older
                if (age > 16 || (age == 16 &&
                            (today.get(Calendar.MONTH) > selectedCalendar.get(Calendar.MONTH) ||
                                    (today.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                                            today.get(Calendar.DAY_OF_MONTH) >= selectedCalendar.get(Calendar.DAY_OF_MONTH))))) {

                    dob = "$dayOfMonth/${month + 1}/$year"
                } else {
                    Toast.makeText(context, "You must be at least 16 years old", Toast.LENGTH_SHORT).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

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
            value = username,
            onValueChange = { username = it },
            singleLine = true,
            label = { Text("Username") }
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)) {

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                singleLine = true,
                label = { Text("DOB") },
                readOnly = true, // Prevents editing
                modifier = Modifier.fillMaxWidth()
            )

            // Transparent clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { datePickerDialog.show() }
            )
        }


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
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            singleLine = true,
            label = { Text("Confirm Password") },
            visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible1 = !passwordVisible1 }) {
                    Icon(
                        imageVector = if (passwordVisible1) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible1) "Hide password" else "Show password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        val buttonColor = if (isSystemInDarkTheme()) Color(0xFF448AFF) else Color(0xFF536DFE)
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            ,onClick = {
                authSetup(viewModel,
                name,
                username,
                dob,
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

    if (viewModel.checkValidation(name, phone, password, email, confirmPassword)) {
        viewModel.signUp(name.trim(), email.trim(), phone.trim(), password.trim()) { isSuccess, message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (isSuccess) {
                viewModel.viewModelScope.launch {
                    onSuccess() // Navigate to the main screen
                }
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
