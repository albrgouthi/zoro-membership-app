package com.zoro.membership

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MembershipScreen(context: Context, user: AuthClient.AuthUser, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(user.email ?: "Member", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Zoro Member", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Free Tier — Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Member ID", style = MaterialTheme.typography.labelSmall)
                        Text(user.id.take(8).uppercase(), fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Level", style = MaterialTheme.typography.labelSmall)
                        Text("Free", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Status", style = MaterialTheme.typography.labelSmall)
                        Text("Active", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { /* upgrade flow comes with Stripe integration */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage Membership")
        }

        Spacer(Modifier.height(24.dp))

        SettingsRow("Update your profile information")
        SettingsRow("Change your login password")
        SettingsRow("Change language")
        SettingsRow("Suggest a partner")
        SettingsRow("Issues & Suggestions")

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                AuthClient.clearSession(context)
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}

@Composable
private fun SettingsRow(label: String) {
    ListItem(headlineContent = { Text(label) })
    Divider()
}
