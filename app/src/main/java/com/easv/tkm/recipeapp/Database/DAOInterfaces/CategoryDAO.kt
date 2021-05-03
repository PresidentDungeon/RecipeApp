package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.room.Dao
import androidx.room.Insert
import com.easv.tkm.recipeapp.data.Models.Category

@Dao
interface CategoryDAO {
    @Insert
    fun addCategory(category: Category)
}