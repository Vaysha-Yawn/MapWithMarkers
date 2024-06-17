package com.appio.testapp.presenter

import android.content.Context
import android.widget.Toast
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.util.execute
import com.appio.testapp.mmodel.MapPinEntity
import com.appio.testapp.mmodel.SaveRoom
import com.appio.testapp.view.DisplayRouteOnMap
import com.google.android.gms.maps.model.LatLng

class MapPresenter() {

    val markers = mutableListOf<MapPinEntity>()
    private lateinit var save: SaveRoom
    private lateinit var displayRoute: DisplayRouteOnMap
    private lateinit var showError: ViewShowError

    fun start(showError: ViewShowError, saveRoom: SaveRoom, displayRoute: DisplayRouteOnMap) {
        save = saveRoom
        this.showError = showError
        this.displayRoute = displayRoute
        getAllSavedPins()
    }

    private fun getAllSavedPins() {
        save.getAllPins { pins ->
            if (pins.isNotEmpty()) {
                markers.addAll(pins)
            }
        }
    }

    fun addMarker(name: String, position: LatLng) {
        val pinEnt = MapPinEntity(0, name, position.latitude, position.longitude)
        markers.add(pinEnt)
        save.add(pinEnt)
    }


    fun deleteMarker(pin: LatLng) {
        var neededMarker: MapPinEntity? = null
        markers.forEach {
            if (it.longitude == pin.longitude && it.latitude == pin.latitude) {
                neededMarker = it
            }
        }
        if (neededMarker != null) {
            save.delete(neededMarker!!)
            markers.remove(neededMarker)
        }
    }

    fun calculateAndDisplayRoute(start: LatLng, end: LatLng) {
        GoogleDirection.withServerKey("Your_key")
            .from(start)
            .to(end)
            .avoid(AvoidType.FERRIES)
            .avoid(AvoidType.HIGHWAYS)
            .execute(
                onDirectionSuccess = { direction ->
                    if (direction!!.isOK()) {
                        displayRoute.displayRoute(direction)
                    } else {
                        showError.errorHandle(direction.status)
                    }
                },
                onDirectionFailure = { t: Throwable ->
                    t.message.let {
                        showError.errorHandle(it!!)
                    }
                }
            )
    }
}