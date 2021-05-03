package com.easv.tkm.recipeapp.data

import android.app.Application
import com.easv.tkm.recipeapp.DAL.RecipeRepository

class RecipeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RecipeRepository.initialize(this)
    }
}