package com.example.chattogether.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Frequently Asked Questions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // List of FAQs
            FAQItem(
                question = "How do I reset my password?",
                answer = "To reset your password, go to the Login screen and tap on 'Forgot Password'. You will receive an email with instructions to reset your password."
            )
            FAQItem(
                question = "How do I contact customer support?",
                answer = "You can reach out to our support team via email at support@chattogether.com or through the 'Contact Support' button below."
            )
            FAQItem(
                question = "Can I delete my account?",
                answer = "Yes, you can request account deletion by contacting our support team. Note that once deleted, all your data will be permanently removed."
            )
            FAQItem(
                question = "How do I change my profile picture?",
                answer = "To change your profile picture, go to the 'Profile' section and tap on your current profile picture to upload a new one."
            )
            FAQItem(
                question = "Why am I not receiving notifications?",
                answer = "Ensure that notifications are enabled in your device settings and within the app settings under 'Notifications'."
            )
            FAQItem(
                question = "How can I block a user?",
                answer = "Go to the user's profile, tap on the options menu (three dots), and select 'Block User'. You will no longer receive messages from them."
            )
            FAQItem(
                question = "How can I recover deleted messages?",
                answer = "Unfortunately, once messages are deleted, they cannot be recovered. We recommend backing up important conversations."
            )
            FAQItem(
                question = "Is my data secure?",
                answer = "Yes, we use end-to-end encryption to ensure your messages and personal data remain private and secure."
            )
            FAQItem(
                question = "How do I log out of my account?",
                answer = "You can log out by going to the 'Profile' screen and tapping on the 'Log Out' button at the bottom of the page."
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Contact Support Button
            Button(
                onClick = { /* Open support email or chat */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Contact Support", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded }
    ) {
        Text(text = question, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if (expanded) {
            Text(
                text = answer,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Divider(modifier = Modifier.padding(vertical = 6.dp))
    }
}
