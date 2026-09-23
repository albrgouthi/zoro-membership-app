package com.zoro.membership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZoroTheme {
                ZoroApp()
            }
        }
    }
}

@Composable
fun ZoroApp() {
    var language by remember { mutableStateOf("en") }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                categories = SupabaseClient.getCategories()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zoro") },
                actions = {
                    LanguageToggle(current = language, onChange = { language = it })
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    "Couldn't load categories: $errorMessage",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                categories.isEmpty() -> Text(
                    "No categories yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.nameFor(language)) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageToggle(current: String, onChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf("en", "es", "ar").forEach { code ->
            TextButton(onClick = { onChange(code) }) {
                Text(code.uppercase(), fontWeight = if (code == current) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
