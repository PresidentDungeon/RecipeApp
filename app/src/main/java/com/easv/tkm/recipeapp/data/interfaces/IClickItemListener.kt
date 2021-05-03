package com.easv.tkm.recipeapp.data.interfaces

import com.easv.tkm.recipeapp.data.Models.Recipe

interface IClickItemListener {
    fun onItemClick(recipe: Recipe, position: Int)
}