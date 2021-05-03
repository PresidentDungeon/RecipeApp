package com.easv.tkm.recipeapp.data.Models

import androidx.room.Entity

@Entity(primaryKeys = ["recipeID", "ingredientID"])
data class RecipeIngredientEntry (val recipeID: Int, val ingredientID: Int)