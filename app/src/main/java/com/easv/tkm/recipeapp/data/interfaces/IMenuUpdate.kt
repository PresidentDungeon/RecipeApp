package com.easv.tkm.recipeapp.data.interfaces

import com.easv.tkm.recipeapp.data.Sorting

interface IMenuUpdate {
    fun updateMenu(sortingType: Sorting)
}