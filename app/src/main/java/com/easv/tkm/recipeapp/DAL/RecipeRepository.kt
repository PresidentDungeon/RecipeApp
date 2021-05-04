package com.easv.tkm.recipeapp.DAL

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import com.easv.tkm.recipeapp.Database.Database
import com.easv.tkm.recipeapp.data.Models.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.IllegalStateException

private const val DATABASE_NAME = "recipe-database"

class RecipeRepository private constructor (context: Context){

    private val CALLBACK = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) { super.onCreate(db)
            GlobalScope.async { categoryDAO.addCategories(categories) }
        }
    }


    private val database: Database = Room.databaseBuilder(context.applicationContext, Database::class.java, DATABASE_NAME).addCallback(CALLBACK).build()

    private val recipeDAO = database.recipeDAO()
    private val categoryDAO = database.categoryDAO()
    private val ingredientDAO = database.ingredientDAO()

    suspend fun addRecipe(recipe: Recipe, ingredients: List<IngredientEntry>){

        val recipeIngredient: MutableList<RecipeIngredientEntry> = mutableListOf()
        val recipeID = suspend {recipeDAO.addRecipe(recipe)}.invoke().toInt()
        ingredients.forEach { ingredientEntry -> ingredientEntry.recipeID = recipeID }
        val ingredientIDs = suspend {ingredientDAO.addIngredientEntries(ingredients)}.invoke().map {IDLong -> IDLong.toInt()}
        ingredientIDs.forEach { ingredientID -> recipeIngredient.add(RecipeIngredientEntry(recipeID, ingredientID))}
        suspend { recipeDAO.addRecipeIngredient(recipeIngredient) }.invoke()

    }

    suspend fun getRecipes(query: String, args: Array<Any>): List<RecipeWithIngredients> = recipeDAO.getRecipesFilter(SimpleSQLiteQuery(query, args))

    fun addCategory(category: Category){categoryDAO.addCategory(category)}
    fun getCategories(): LiveData<List<Category>> = categoryDAO.getCategories()
    
    companion object{
        private var INSTANCE: RecipeRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){INSTANCE = RecipeRepository(context) }
        }

        fun get(): RecipeRepository{
            return INSTANCE?: throw IllegalStateException("RecipeRepository must be initialized first")
        }

        val categories: List<Category> = listOf(
            Category(0, "Breakfast"),
            Category(0, "Lunch"),
            Category(0, "Dinner"),
            Category(0, "Dessert"),
            Category(0, "Snacks"),
            Category(0, "Drinks"),
        )
    }
}