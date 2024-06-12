package com.appio.testapp.mmodel


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MapPinEntity::class], version = 1, exportSchema = false)
abstract class AppDatabasePin : RoomDatabase() {
    abstract fun dao(): DAOPins
}
