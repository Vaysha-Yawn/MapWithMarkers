package com.appio.testapp.mmodel


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MapPinEntity")
data class MapPinEntity (
    @PrimaryKey(true)
    val id:Int = 0,

    @ColumnInfo(name = "name")
    val name:String = "",

    @ColumnInfo(name = "latitude")
    val latitude:Double = 0.0,

    @ColumnInfo(name = "longitude")
    val longitude:Double = 0.0,
)

