package com.easv.tkm.recipeapp.data.Models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(primaryKeys = ["recipeID", "ingredientID"],
    foreignKeys = [ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeID"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
                ForeignKey(entity = IngredientEntry::class, parentColumns = ["id"], childColumns = ["ingredientID"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),] )
data class RecipeIngredientEntry (val recipeID: Int, val ingredientID: Int)