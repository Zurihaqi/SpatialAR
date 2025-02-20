package com.example.arcorefurniture.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object MainScreenNav

@Serializable
data class ARScreenNav(val model: List<String>)

@Serializable
object CategoryScreenNav

@Serializable
data class FurnitureScreenNav(val categoryItem: String)