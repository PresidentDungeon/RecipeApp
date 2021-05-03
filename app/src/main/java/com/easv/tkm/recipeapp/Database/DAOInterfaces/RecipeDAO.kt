package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.room.Dao
import androidx.room.Query
import com.easv.tkm.recipeapp.data.Models.RecipeWithCategories

@Dao
interface RecipeDAO {

    @Query("SELECT * FROM Recipe")
    fun getRecipesWithCategoriesAndIngredientEntries(): List<RecipeWithCategories>
}