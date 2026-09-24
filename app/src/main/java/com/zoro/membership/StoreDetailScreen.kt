package com.zoro.membership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
fun RedeemScreen(offer: Offer, memberId: String, language: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(28.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Show this to the cashier", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(offer.titleFor(language), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Text(
                    memberId.take(8).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text("Member Code", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "A scannable QR version of this code is coming in the next update.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
