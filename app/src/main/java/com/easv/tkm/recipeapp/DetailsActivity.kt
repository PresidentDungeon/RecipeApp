package com.easv.tkm.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easv.tkm.recipeapp.data.Models.Recipe
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
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