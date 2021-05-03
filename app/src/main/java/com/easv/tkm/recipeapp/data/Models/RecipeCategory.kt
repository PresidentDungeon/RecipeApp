package com.easv.tkm.recipeapp.data.Models

import androidx.room.Entity

@Entity(primaryKeys = ["recipeID", "categoryID"])
data class RecipeCategory (val recipeID: Int, val categoryID: Int)