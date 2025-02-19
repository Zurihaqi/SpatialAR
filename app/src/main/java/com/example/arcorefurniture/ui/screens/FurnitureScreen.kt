package com.example.arcorefurniture.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.arcorefurniture.ui.navigation.ARScreenNav

@Composable
fun FurnitureScreen(navController: NavController, categoryItem: String) {
    var models = mutableListOf<String>("MASUK ARCORE","")
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {navController.navigate(ARScreenNav(models))}) {
            Text(text = categoryItem)
        }

    }

}