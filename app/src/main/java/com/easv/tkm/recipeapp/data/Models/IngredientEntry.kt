package com.easv.tkm.recipeapp.data.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(foreignKeys = [ForeignKey(entity = Recipe::class, parentColumns = ["id"], childColumns = ["recipeID"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)])
data class IngredientEntry(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "recipeID")
    var recipeID: Int = 0,
    var name: String,
    var amount: Double,
    var measurementUnit: String) : Serializable {}