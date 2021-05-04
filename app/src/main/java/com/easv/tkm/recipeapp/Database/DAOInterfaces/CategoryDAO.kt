package com.easv.tkm.recipeapp.Database.DAOInterfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easv.tkm.recipeapp.data.Models.Category

@Dao
interface CategoryDAO {
    @Insert
    fun addCategory(category: Category)

    @Insert
    fun addCategories(categories: List<Category>)

    @Query("SELECT * FROM Category")
    fun getCategories(): LiveData<List<Category>>
}