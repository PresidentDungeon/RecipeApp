package com.easv.tkm.recipeapp.data.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryID"], onDelete = CASCADE)])
data class Recipe(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "categoryID")
    var categoryID: Int = 0,
    var title: String,
    var description: String,
    var preparations: String,
    var imageURL: String) : Serializable{}