package com.easv.tkm.recipeapp.GUI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.easv.tkm.recipeapp.DAL.RecipeRepository
import com.easv.tkm.recipeapp.R
import com.easv.tkm.recipeapp.data.Models.Category
import com.easv.tkm.recipeapp.data.Models.Recipe
import kotlinx.android.synthetic.main.activity_crud.*
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_details.tvDescription
import kotlinx.android.synthetic.main.activity_details.tvPreparations
import kotlinx.android.synthetic.main.activity_details.tvTitle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class DetailsActivity : AppCompatActivity() {

    private var recipeRepository = RecipeRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    fun initializeText(recipe: Recipe) {
        tvTitle.setText(recipe.title)
        tvDescription.setText(recipe.description)
        tvPreparations.setText(recipe.preparations)
    }
}