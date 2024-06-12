package com.appio.testapp.mmodel


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DAOPins {
    @Query("SELECT * FROM MapPinEntity")
    suspend fun getAll(): List<MapPinEntity>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pin: MapPinEntity)

    @Delete
    suspend fun delete(pin: MapPinEntity)

}
