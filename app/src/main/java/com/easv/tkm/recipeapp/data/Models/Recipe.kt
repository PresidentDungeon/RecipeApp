package com.easv.tkm.recipeapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, var title: String, var description: String, var preparations: String,
    var imageURL: String) : Serializable{}