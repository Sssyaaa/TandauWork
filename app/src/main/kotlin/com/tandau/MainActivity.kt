package com.tandau

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tandau.data.local.database.TandauDatabase
import com.tandau.data.repository.UserRepository
import com.tandau.navigation.TandauNavGraph
import com.tandau.ui.theme.TandauTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TandauDatabase.getDatabase(this)
        val userRepository = UserRepository(database)

        setContent {
            TandauTheme {
                val navController = rememberNavController()
                TandauNavGraph(navController, userRepository)
            }
        }
    }
}