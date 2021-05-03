package com.easv.tkm.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.data.Models.Category
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

class MainActivity : AppCompatActivity() {

    private var recipeRepository = RecipeRepository.get()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val category = Category(0, "TestCategory")
//
//        var job: Deferred<Unit>? = null
//        job = GlobalScope.async { recipeRepository.addCategory(category) }
//        job.invokeOnCompletion {  }



    }
}