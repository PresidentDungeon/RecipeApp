package com.easv.tkm.recipeapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class IngredientEntry(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, var name: String, var measurementUnit: String) : Serializable {}