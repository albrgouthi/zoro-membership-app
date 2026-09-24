package com.zoro.membership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
    var selectedMerchant by remember { mutableStateOf<Merchant?>(null) }
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var redeemingOffer by remember { mutableStateOf<Offer?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val accessToken = AuthClient.getAccessToken(context)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                categories = SupabaseClient.getCategories(accessToken)
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
            merchants = SupabaseClient.getMerchants(category.id, accessToken)
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(selectedMerchant) {
        val merchant = selectedMerchant ?: return@LaunchedEffect
        isLoading = true
        errorMessage = null
        try {
            offers = SupabaseClient.getOffers(merchant.id, accessToken)
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
                            Tab.HOME -> when {
                                redeemingOffer != null -> "Redeem"
                                selectedMerchant != null -> selectedMerchant!!.nameFor(language)
                                selectedCategory != null -> selectedCategory!!.nameFor(language)
                                else -> "Zoro"
                            }
                            Tab.MARKETPLACE -> "Marketplace"
                            Tab.MEMBERSHIP -> "Membership"
                            Tab.ORDERS -> "My Orders"
                        }
                    )
                },
                navigationIcon = {
                    val showBack = currentTab == Tab.HOME &&
                        (selectedCategory != null || selectedMerchant != null || redeemingOffer != null)
                    if (showBack) {
                        IconButton(onClick = {
                            when {
                                redeemingOffer != null -> redeemingOffer = null
                                selectedMerchant != null -> selectedMerchant = null
                                else -> selectedCategory = null
                            }
                        }) {
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
                    redeemingOffer != null -> RedeemScreen(
                        offer = redeemingOffer!!,
                        memberId = user.id,
                        language = language
                    )
                    selectedMerchant != null -> StoreDetailScreen(
                        merchant = selectedMerchant!!,
                        offers = offers,
                        language = language,
                        onRedeem = { redeemingOffer = it }
                    )
                    selectedCategory == null -> CategoryListScreen(
                        categories = categories,
                        language = language,
                        onSelect = { selectedCategory = it }
                    )
                    else -> MerchantListScreen(
                        merchants = merchants,
                        language = language,
                        onSelect = { selectedMerchant = it }
                    )
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Savings at your favorite places",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Browse a category to see live deals",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }
        items(categories) { category ->
            val visual = visualFor(category.icon)
            Card(
                colors = CardDefaults.cardColors(containerColor = visual.color.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onSelect(category) }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(visual.emoji, fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        category.nameFor(language),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = visual.color
                    )
                }
            }
        }
    }
}

@Composable
fun MerchantListScreen(merchants: List<Merchant>, language: String, onSelect: (Merchant) -> Unit) {
    if (merchants.isEmpty()) {
        Text("No stores in this category yet.", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(merchants) { merchant ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelect(merchant) }
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(merchant.nameFor(language).take(1).uppercase(), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(merchant.nameFor(language), fontWeight = FontWeight.Bold)
                        Text(
                            merchant.bioFor(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
