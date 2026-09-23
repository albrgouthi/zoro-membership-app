package com.zoro.membership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoroApp() {
    var language by remember { mutableStateOf("en") }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var merchants by remember { mutableStateOf<List<Merchant>>(emptyList()) }
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

    // When a category is tapped, load its merchants
    LaunchedEffect(selectedCategory) {
        val category = selectedCategory ?: return@LaunchedEffect
        isLoading = true
        errorMessage = null
        try {
            merchants = SupabaseClient.getMerchants(category.id)
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedCategory?.nameFor(language) ?: "Zoro") },
                navigationIcon = {
                    if (selectedCategory != null) {
                        IconButton(onClick = { selectedCategory = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
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
                    "Something went wrong: $errorMessage",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                selectedCategory == null -> CategoryListScreen(
                    categories = categories,
                    language = language,
                    onSelect = { selectedCategory = it }
                )
                else -> MerchantListScreen(merchants = merchants, language = language)
            }
        }
    }
}

@Composable
fun CategoryListScreen(categories: List<Category>, language: String, onSelect: (Category) -> Unit) {
    if (categories.isEmpty()) {
        Text("No categories yet.", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categories) { category ->
            ListItem(
                headlineContent = { Text(category.nameFor(language)) },
                modifier = Modifier.clickable { onSelect(category) }
            )
            Divider()
        }
    }
}

@Composable
fun MerchantListScreen(merchants: List<Merchant>, language: String) {
    if (merchants.isEmpty()) {
        Text("No stores in this category yet.", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(merchants) { merchant ->
            ListItem(
                headlineContent = { Text(merchant.nameFor(language)) },
                supportingContent = { Text(merchant.bioFor(language)) }
            )
            Divider()
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
