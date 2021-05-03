package com.easv.tkm.recipeapp.DAL

import android.content.Context
import androidx.room.Room
import com.easv.tkm.recipeapp.Database.Database
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.RecipeWithCategories
import java.lang.IllegalStateException

private const val DATABASE_NAME = "recipe-database"

class RecipeRepository private constructor (context: Context){
    private val database: Database = Room.databaseBuilder(context.applicationContext, Database::class.java, DATABASE_NAME).build()
    private val recipeDAO = database.recipeDAO()
    private val categoryDAO = database.categoryDAO()

    suspend fun getRecipes(): List<RecipeWithCategories> = recipeDAO.getRecipesWithCategoriesAndIngredientEntries()
    fun addCategory(category: Category){categoryDAO.addCategory(category)}

    companion object{
        private var INSTANCE: RecipeRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){INSTANCE = RecipeRepository(context) }
        }

        fun get(): RecipeRepository{
            return INSTANCE?: throw IllegalStateException("RecipeRepository must be initialized first")
        }

    }
}