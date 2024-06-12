package com.appio.testapp.mmodel

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveRoom(val context: Context) {
    val db = Room.databaseBuilder(
        context,
        AppDatabasePin::class.java, "database-pin"
    ).build()
    val roomDao = db.dao()

    fun getAllPins( use:(List<MapPinEntity>)->Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val pins = roomDao.getAll()
                use(pins)
        }
    }
    fun add( pin: MapPinEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            roomDao.insert(pin)
        }
    }

    fun delete( pin: MapPinEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            roomDao.delete(pin)
        }
    }

}
