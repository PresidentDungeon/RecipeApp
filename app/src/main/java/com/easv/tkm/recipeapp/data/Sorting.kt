package com.easv.tkm.recipeapp.data

enum class Sorting(val query: String) {
    SORTING_NAME(" order by title ASC"),
    SORTING_AGE(" order by id ASC")
}