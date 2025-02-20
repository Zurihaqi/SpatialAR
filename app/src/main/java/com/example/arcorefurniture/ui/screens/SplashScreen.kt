package com.example.arcorefurniture.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arcorefurniture.R
import com.example.arcorefurniture.ui.navigation.MainScreenNav
import kotlinx.coroutines.delay

@Composable
@Preview
fun SplashScreen(navController: NavController = rememberNavController()) {
    val isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(MainScreenNav) {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_img),
                contentDescription = "Splash Background",
                modifier = Modifier.size(300.dp).padding(top = 50.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Splash Logo",
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            )
        }
    }
}
