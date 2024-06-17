package com.appio.testapp.presenter

import com.akexorcist.googledirection.model.Direction

interface DisplayRouteOnMap{
    fun displayRoute(direction: Direction)
}

interface ViewShowError {
    fun errorHandle(errorMessage: String)
}