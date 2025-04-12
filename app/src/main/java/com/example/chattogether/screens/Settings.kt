package com.example.chattogether.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.components.ExpandableFAQItem
import com.example.chattogether.navigation.Screen
import com.example.chattogether.viewmodel.ProfileViewModel

@Composable
fun SettingsScreen(profileViewModel: ProfileViewModel = viewModel(),
                   navController: NavController,
                   onThemeChange: (Boolean) -> Unit,
                   onLoginClick: () -> Unit) {
    var isDarkTheme by rememberSaveable { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Account Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsItem(title = "Edit Profile") {
                navController.navigate(Screen.Profile.route
                    .replace("{isEditing}", true.toString()))
            }

            SettingsItem(title = "Change Password") {
                navController.navigate(Screen.ChangePassword.route)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "App Preferences",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SwitchSettingItem(
                title = "Dark Mode",
                checked = isDarkTheme,
                onCheckedChange = {
                    onThemeChange(it)
                }
            )

//            SwitchSettingItem(
//                title = "Notifications",
//                checked = notificationsEnabled,
//                onCheckedChange = { notificationsEnabled = it }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Help & Support",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsItem(title = "FAQs") {  }
            ExpandableFAQItem(
                question = "How do I reset my password?",
                answer = "Go to the Login screen and tap 'Forgot Password'. You'll receive an email with reset instructions."
            )
            ExpandableFAQItem(
                question = "How do I contact customer support?",
                answer = "Reach us via email at support@chattogether.com or use the 'Contact Support' button."
            )
            ExpandableFAQItem(
                question = "Can I delete my account?",
                answer = "Yes, contact support to request account deletion. All your data will be permanently removed."
            )
            ExpandableFAQItem(
                question = "Why am I not receiving notifications?",
                answer = "Ensure notifications are enabled in your device and app settings."
            )
            SettingsItem(title = "Contact Us") { /* Navigate to Contact Page */ }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onLoginClick()
                    profileViewModel.logout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
    Divider()
}

@Composable
fun SwitchSettingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    Divider()
}


