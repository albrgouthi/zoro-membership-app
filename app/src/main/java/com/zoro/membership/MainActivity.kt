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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.List as ListIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZoroTheme {
                ZoroRoot()
            }
        }
    }
}

@Composable
fun ZoroRoot() {
    val context = LocalContext.current
    var user by remember { mutableStateOf(AuthClient.getSession(context)) }

    if (user == null) {
        AuthScreen(context = context, onAuthenticated = { user = it })
    } else {
        ZoroApp(user = user!!, onSignOut = { user = null })
    }
}

private enum class Tab { HOME, MARKETPLACE, MEMBERSHIP, ORDERS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoroApp(user: AuthClient.AuthUser, onSignOut: () -> Unit) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(Tab.HOME) }

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
                title = {
                    Text(
                        when (currentTab) {
                            Tab.HOME -> selectedCategory?.nameFor(language) ?: "Zoro"
                            Tab.MARKETPLACE -> "Marketplace"
                            Tab.MEMBERSHIP -> "Membership"
                            Tab.ORDERS -> "My Orders"
                        }
                    )
                },
                navigationIcon = {
                    if (currentTab == Tab.HOME && selectedCategory != null) {
                        IconButton(onClick = { selectedCategory = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentTab == Tab.HOME) {
                        LanguageToggle(current = language, onChange = { language = it })
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == Tab.HOME,
                    onClick = { currentTab = Tab.HOME },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.MARKETPLACE,
                    onClick = { currentTab = Tab.MARKETPLACE },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                    label = { Text("Marketplace") }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.MEMBERSHIP,
                    onClick = { currentTab = Tab.MEMBERSHIP },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text("Membership") }
                )
                NavigationBarItem(
                    selected = currentTab == Tab.ORDERS,
                    onClick = { currentTab = Tab.ORDERS },
                    icon = { Icon(Icons.Filled.ListIcon, contentDescription = null) },
                    label = { Text("My Orders") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (currentTab) {
                Tab.HOME -> when {
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
                Tab.MARKETPLACE -> ComingSoon("Marketplace")
                Tab.MEMBERSHIP -> MembershipScreen(context = context, user = user, onSignOut = onSignOut)
                Tab.ORDERS -> ComingSoon("My Orders")
            }
        }
    }
}

@Composable
fun ComingSoon(label: String) {
    Text("$label — coming soon", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
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
