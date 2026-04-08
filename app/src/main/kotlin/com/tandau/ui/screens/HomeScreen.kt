package com.tandau.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tandau.data.local.entities.UserSession
import com.tandau.data.repository.UserRepository
import com.tandau.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, userRepository: UserRepository) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var userSession by remember { mutableStateOf<UserSession?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userSession = userRepository.getSession()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E1E),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                val items = listOf(
                    NavigationItem("Home", Icons.Default.Home),
                    NavigationItem("Events", Icons.Default.DateRange),
                    NavigationItem("Guides", Icons.Default.MenuBook),
                    NavigationItem("Community", Icons.Default.People),
                    NavigationItem("Profile", Icons.Default.Person)
                )
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> HomeTab(userSession?.userEmail ?: "User")
                1 -> PlaceholderTab("Events")
                2 -> PlaceholderTab("Guides")
                3 -> PlaceholderTab("Community")
                4 -> ProfileTab(userSession?.userEmail ?: "User") {
                    scope.launch {
                        userRepository.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTab(userEmail: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Text(
                text = "TANDAU",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = userEmail.substringBefore("@"),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            SectionCard("My Next Event", "Digital Strategy Workshop", "Tomorrow, 10:00 AM", Icons.Default.Event)
            Spacer(modifier = Modifier.height(16.dp))
            SectionCard("Trending Guide", "Mastering Personal Brand", "5 min read", Icons.Default.Lightbulb)
            Spacer(modifier = Modifier.height(16.dp))
            SectionCard("New Message", "Community Discussion", "2 new replies", Icons.Default.Chat)
        }
    }
}

@Composable
fun SectionCard(category: String, title: String, subtitle: String, icon: ImageVector) {
    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PlaceholderTab(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$name Content Coming Soon", color = Color.Gray, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ProfileTab(email: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = email, color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
        ) {
            Text("Logout")
        }
    }
}

data class NavigationItem(val title: String, val icon: ImageVector)
