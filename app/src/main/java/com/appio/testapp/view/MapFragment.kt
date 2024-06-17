package com.appio.testapp.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.appio.testapp.R
import com.appio.testapp.databinding.FragmentMapsBinding
import com.appio.testapp.mmodel.SaveRoom
import com.appio.testapp.presenter.DisplayRouteOnMap
import com.appio.testapp.presenter.MapPresenter
import com.appio.testapp.presenter.ViewShowError
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, DisplayRouteOnMap, ViewShowError {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    private val mapPresenter = MapPresenter()
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapPresenter.start(this, SaveRoom(requireContext()), this)
        setMyLocByService()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        val  supportMapFragment:SupportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.isBuildingsEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setMinZoomPreference(10f)
        googleMap.setOnMapLongClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)
        setMyLoc()
        updateMarkers()
    }

    @SuppressLint("MissingPermission")
    fun setMyLocByService() {
        val serv = LocationServices.getFusedLocationProviderClient(requireActivity())
        serv.lastLocation.addOnSuccessListener { loc ->
            currentLocation = loc
            if (currentLocation != null) {
                val meLoc = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(meLoc))
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        setMyLoc()
        return false
    }

    @SuppressLint("MissingPermission")
    private fun setMyLoc() {
        currentLocation = mMap.myLocation
        if (currentLocation != null) {
            val meLoc = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(meLoc))
        }
    }

    override fun onMapLongClick(pin: LatLng) {
        setNameDialog() { name ->
            mapPresenter.addMarker(name, pin)
            binding.trash.setOnClickListener {
                deleteMarker(pin)
            }
            binding.route.setOnClickListener {
                addDistanceBetweenMarkerAndPosition(pin)
                //calculateAndDisplayRoute(p0)
            }
            updateMarkers()
        }
    }

    private fun markerButtonsVisibility(turn:Boolean){
        if (turn){
            binding.trash.visibility = View.VISIBLE
            binding.route.visibility = View.VISIBLE
        }else{
            binding.trash.visibility = View.GONE
            binding.route.visibility = View.GONE
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        markerButtonsVisibility(true)
        binding.trash.setOnClickListener {
            deleteMarker(marker.position)
        }
        binding.route.setOnClickListener {
            addDistanceBetweenMarkerAndPosition(marker.position)
            //calculateAndDisplayRoute(p0)
        }
        return false
    }

    override fun onMapClick(p0: LatLng) {
        updateMarkers()
        markerButtonsVisibility(false)
    }

    private fun updateMarkers() {
        mMap.clear()
        mapPresenter.markers.forEach {
            mMap.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(it.name)
            )
        }
    }

    private fun setNameDialog(next: (String) -> Unit) {
        val d = Dialog(requireContext())
        d.setContentView(R.layout.dialog_set_name_marker)
        val apply = d.findViewById<TextView>(R.id.apply)
        val et = d.findViewById<EditText>(R.id.textEdit)
        apply.setOnClickListener {
            val text = et.text.toString()
            if (text != "") {
                next(text)
            }
            d.dismiss()
        }
        d.show()
    }

    private fun addDistanceBetweenMarkerAndPosition(end: LatLng) {
        updateMarkers()
        val start = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        val opt = PolylineOptions()
        opt.add(start, end)
        val color = ContextCompat.getColor(requireContext(), R.color.rout)
        opt.color(color)
        mMap.addPolyline(opt)
    }

    private fun deleteMarker(pin: LatLng) {
        mapPresenter.deleteMarker(pin)
        updateMarkers()
        markerButtonsVisibility(false)
    }

    fun calculateAndDisplayRoute(end: LatLng) {
        val start = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        mapPresenter.calculateAndDisplayRoute(start, end)
    }

    override fun displayRoute(direction: Direction){
        for (i in 0 until direction.routeList.size) {
            val route = direction.routeList[i]
            val color = ContextCompat.getColor(requireContext(), R.color.rout)
            val directionPositionList = route.legList[0].directionPoint
            mMap.addPolyline(
                DirectionConverter.createPolyline(
                    requireContext(),
                    directionPositionList,
                    5,
                    color
                )
            )
        }
    }

    override fun errorHandle(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

}

