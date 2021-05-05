package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.easv.tkm.recipeapp.data.Models.Recipe
import com.easv.tkm.recipeapp.data.Models.RecipeIngredientEntry
import com.easv.tkm.recipeapp.data.Models.RecipeWithIngredients

@Dao
interface RecipeDAO {

    @Insert
    suspend fun addRecipe(recipe: Recipe): Long

    @Insert
    suspend fun addRecipeIngredient(recipeIngredientEntry: List<RecipeIngredientEntry>)

    @RawQuery()
    suspend fun getRecipesFilter(query: SupportSQLiteQuery): List<RecipeWithIngredients>

    @Query("SELECT * FROM Recipe WHERE id =:recipeID")
    suspend fun getRecipeByID(recipeID: Long): RecipeWithIngredients

    @Update
    fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM RecipeIngredientEntry WHERE recipeID = :recipeID")
    suspend fun deleteRecipeIngredients(recipeID: Long)

}