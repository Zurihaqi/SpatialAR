package com.example.arcorefurniture.seeder

data class Furniture(
    val name: String,
    val models: String,
    val thumbnail: String,
    val category: String,
    val detail: String
)

object ListFurniture{
    val listFurniture = listOf(
        Furniture(
            name = "Sofa 1",
            models = "models/sofas/Sofa_01_1k.gltf/Sofa_01_1k.gltf",
            thumbnail = "",
            category = "Sofa",
            detail = ""
        ),
        Furniture(
            name = "Sofa 2",
            models = "models/sofas/sofa_03_1k.gltf/sofa_03_1k.gltf",
            thumbnail = "",
            category = "Sofa",
            detail = ""
        ),
        Furniture(
            name = "Chinese Cabinet",
            models = "models/tables/chinese_cabinet_1k.gltf/chinese_cabinet_1k.gltf",
            thumbnail = "",
            category = "Table",
            detail = ""
        ),
        Furniture(
            name = "Coffee Table 01",
            models = "models/tables/coffee_table_round_01_1k.gltf/coffee_table_round_01_1k.gltf",
            thumbnail = "",
            category = "Table",
            detail = ""
        ),
        Furniture(
            name = "Modern table 01",
            models = "models/tables/modern_wooden_cabinet_1k.gltf/modern_wooden_cabinet_1k.gltf",
            thumbnail = "",
            category = "Table",
            detail = ""
        ),

    )

    fun getFurnitureByCategory(category: String): Furniture? {
        return listFurniture.find { it.name.contains(category, ignoreCase = true) }
    }

}