package com.example.arcorefurniture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.arcorefurniture.ui.navigation.ARScreenNav
import com.example.arcorefurniture.ui.navigation.CategoryScreenNav
import com.example.arcorefurniture.ui.navigation.FurnitureScreenNav
import com.example.arcorefurniture.ui.navigation.MainScreenNav
import com.example.arcorefurniture.ui.screens.ARScreen
import com.example.arcorefurniture.ui.screens.CategoryScreen
import com.example.arcorefurniture.ui.screens.FurnitureScreen
import com.example.arcorefurniture.ui.screens.MainScreen
import com.example.arcorefurniture.ui.theme.ARCoreFurnitureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARCoreFurnitureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MainScreenNav,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<MainScreenNav> {
                            MainScreen(navController)
                        }
                        composable<ARScreenNav> {
                            val alphabet = it.toRoute<ARScreenNav>().model
                            ARScreen(navController,alphabet)
                        }
                        composable<CategoryScreenNav>{
                            CategoryScreen(navController)
                        }
                        composable<FurnitureScreenNav>{
                            FurnitureScreen(navController)
                        }


                    }
                }
            }
        }
    }
}

