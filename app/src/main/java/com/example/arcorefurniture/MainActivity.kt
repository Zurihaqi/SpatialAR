package com.example.arcorefurniture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcorefurniture.ui.navigation.ARScreenNav
import com.example.arcorefurniture.ui.navigation.FurnitureScreenNav
import com.example.arcorefurniture.ui.navigation.MainScreenNav
import com.example.arcorefurniture.ui.screens.*
import com.example.arcorefurniture.ui.theme.ARCoreFurnitureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARCoreFurnitureTheme {
                var showSheet by remember { mutableStateOf(false) }
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash_screen") {
                            SplashScreen(navController)
                        }
                        composable<MainScreenNav> {
                            MainScreen(
                                navController = navController,
                            )
                        }
                        composable<ARScreenNav> { backStackEntry ->
                            val model = backStackEntry.arguments?.getString("selectedModel") ?: ""
                            ARScreen(
                                navController = navController,
                                selectedModel = model,
                                onShowCategorySheet = { showSheet = true }
                            )
                        }

                        composable<FurnitureScreenNav> { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("categoryItem")
                            if (category != null) {
                                FurnitureScreen(navController, category)
                            }
                        }
                    }

                    if (showSheet) {
                        CategoryScreen(
                            parentNavController = navController,
                            onDismiss = { showSheet = false }
                        )
                    }
                }
            }
        }
    }
}