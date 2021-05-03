package com.easv.tkm.recipeapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [], version = 1)

@TypeConverters()
abstract class Database : RoomDatabase() {

}