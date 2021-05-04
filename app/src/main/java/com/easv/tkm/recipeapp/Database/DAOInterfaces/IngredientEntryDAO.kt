package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.room.Dao
import androidx.room.Insert
import com.easv.tkm.recipeapp.data.Models.IngredientEntry

@Dao
interface IngredientEntryDAO {
    @Insert
    fun addIngredientEntries(ingredientEntry: List<IngredientEntry>): Array<Long>
}