package com.pepivsky.mapas

import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.log2

class MainActivity : AppCompatActivity(), OnMapReadyCallback { //heredar de onMapReadyCallback

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //createFragment()


        //val address = "Francisco I Madero 135, Manzana 4, 42760 Atengo, Hgo."
        val address = "Calle 30A ##82a-26, Medellín, Antioquia"
        //6.232871692486546, -75.60390812744913


        val latLng = getLocationByAddress(this, address)
        Log.i("latlong", "oncreate latLong:$latLng")

    }

    private fun createFragment() { //metodo que crea el fragment con el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment //obteniendo el fragment map del xml

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //cuando el mapa es creado lo guardamos en la variable map
        map = googleMap
        //cuando el mapa esta listo creamos el marcador
        createMarker()
    }

    private fun createMarker() {
        //cordenadas del punto para crear el marcador en el mapa
        val coordinates = LatLng(28.043893, -16.539329) //recibe dos double
        val marker = MarkerOptions().position(coordinates).title("Mi playa fav")
        map.addMarker(marker) //anadiendo el marcado

        //animar la camara con un zoom al marcador
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 78f),
            400,
            null
        )
    }

    fun getLocationByAddress(context: Context, strAddress: String?): LatLng? { //recibe maximo 5 argumentos para obtener la latitud y longitud
        val coder = Geocoder(context)
        try {
            val address = coder.getFromLocationName(strAddress, 5) ?: return null
            val location = address.first()
            return LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            Log.e("bad address", "algo salio mal")
        }
        return null
    }
}