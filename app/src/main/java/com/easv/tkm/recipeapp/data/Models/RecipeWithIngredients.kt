package com.easv.tkm.recipeapp.data.Models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeWithIngredients (
    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "id",
        entity = IngredientEntry::class,
        entityColumn = "id",
        associateBy = Junction(value = RecipeIngredientEntry::class, parentColumn = "recipeID", entityColumn = "ingredientID")
    )
    val ingredientEntries: List<IngredientEntry>
)