package com.pepivsky.mapas.fragment

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pepivsky.mapas.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createFragment()

        //obteniendo latLong
        val address = "Calle 30A ##82a-26, Medellín, Antioquia, Colombia"
        //6.232871692486546, -75.60390812744913

        val latLng = activity?.baseContext?.let { getLocationByAddress(it, address) }
        Log.i("latlong", "oncreate latLong:$latLng")
    }



    private fun createFragment() { //metodo que crea el fragment con el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment //obteniendo el fragment map del xml

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //cuando el mapa es creado lo guardamos en la variable map
        map = googleMap
        //cuando el mapa esta listo creamos el marcador
        createMarker()
    }

    private fun createMarker() {
        // objeto para crear un bitmap a partir de un drawable
        val pickupMarkerDrawable = resources.getDrawable(R.drawable.ic_android,null)

        //cordenadas del punto para crear el marcador en el mapa
        val coordinates = LatLng(28.043893, -16.539329) //recibe dos double
        val marker = MarkerOptions()
            .position(coordinates)
            .title("Mi playa fav")
            .icon(BitmapDescriptorFactory.fromBitmap(pickupMarkerDrawable.toBitmap(pickupMarkerDrawable.intrinsicWidth, pickupMarkerDrawable.intrinsicHeight, null)))
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