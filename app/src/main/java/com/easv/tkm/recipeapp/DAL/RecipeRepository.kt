package com.easv.tkm.recipeapp.DAL

import android.content.Context
import androidx.room.Room
import com.easv.tkm.recipeapp.Database.Database
import java.lang.IllegalStateException

private const val DATABASE_NAME = "recipe-database"

class RecipeRepository private constructor (context: Context){
    private val database: Database = Room.databaseBuilder(context.applicationContext, Database::class.java, DATABASE_NAME).build()


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