package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.easv.tkm.recipeapp.data.Models.RecipeWithCategories

@Dao
interface RecipeDAO {

    @RawQuery()
    suspend fun getRecipesFilter(query: SupportSQLiteQuery): List<RecipeWithCategories>
}