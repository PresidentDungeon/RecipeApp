package com.easv.tkm.recipeapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.easv.tkm.recipeapp.Database.DAOInterfaces.CategoryDAO
import com.easv.tkm.recipeapp.Database.DAOInterfaces.RecipeDAO
import com.easv.tkm.recipeapp.data.Models.*

@Database(entities = [Recipe::class, Category::class, IngredientEntry::class, RecipeCategory::class, RecipeIngredientEntry::class], version = 1)

@TypeConverters()
abstract class Database : RoomDatabase() {
    abstract fun recipeDAO(): RecipeDAO
    abstract fun categoryDAO(): CategoryDAO
}