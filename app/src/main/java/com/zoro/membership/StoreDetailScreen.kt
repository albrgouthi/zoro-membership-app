package com.zoro.membership

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StoreDetailScreen(
    merchant: Merchant,
    offers: List<Offer>,
    language: String,
    onRedeem: (Offer) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(merchant.nameFor(language), style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text(merchant.bioFor(language), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(20.dp))
            Text("Available Offers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (offers.isEmpty()) {
            item { Text("No active offers right now — check back soon.") }
        }
        items(offers) { offer ->
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(offer.summary(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    offer.condition()?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(offer.titleFor(language))
                    if (offer.termsFor(language).isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            offer.termsFor(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { onRedeem(offer) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Redeem")
                    }
                }
            }
        }
    }
}

@Composable
fun RedeemScreen(offer: Offer?, user: AuthClient.AuthUser, language: String) {
    var token by remember { mutableStateOf(rotatingToken(user.id)) }
    var qrBitmap by remember { mutableStateOf(generateQrBitmap(token)) }

    LaunchedEffect(user.id) {
        while (true) {
            delay(1000)
            val fresh = rotatingToken(user.id)
            if (fresh != token) {
                token = fresh
                qrBitmap = generateQrBitmap(fresh)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        offer?.let {
            Text("Redeeming: ${it.titleFor(language)}", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
        }

        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(28.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(user.email ?: "Zoro Member", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Free Tier — No expiration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Membership QR code",
                    modifier = Modifier.size(220.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Refreshes automatically — a screenshot stops working within 30 seconds",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
