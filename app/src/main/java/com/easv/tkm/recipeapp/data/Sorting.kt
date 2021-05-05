package com.easv.tkm.recipeapp.data

enum class Sorting(val query: String) {
    SORTING_NAME(" order by title COLLATE NOCASE ASC"),
    SORTING_NAME_DESC(" order by title COLLATE NOCASE DESC"),
    SORTING_AGE(" order by id COLLATE NOCASE ASC"),
    SORTING_AGE_DESC(" order by id COLLATE NOCASE DESC")
}