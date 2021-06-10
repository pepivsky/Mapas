package com.pepivsky.mapas.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pepivsky.mapas.MainActivity
import com.pepivsky.mapas.R
import com.pepivsky.mapas.response.GetNotariesResponse
import com.pepivsky.mapas.response.GetNotariesResponseItem
import com.pepivsky.mapas.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    private val TAG = "MainFragment"

    // lista
    val listaNotarias = mutableListOf<GetNotariesResponseItem>()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        requestLocatiopnPermission()


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

        //createFragment()

        //obteniendo latLong
        val address = "Calle 30A ##82a-26, Medellín, Antioquia, Colombia"
        //6.232871692486546, -75.60390812744913

        //val latLng = activity?.baseContext?.let { getLocationByAddress(it, address) }
        //Log.i("latlong", "oncreate latLong:$latLng")


        // createFragment()



    }

    private fun isLocationPermissionGranted () = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun requestLocatiopnPermission() {
        if (isLocationPermissionGranted()) {
            Log.d(TAG, "Hay permiso :)")
            // ejecutar accion
            // getcurrentLocation()

            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener {
                if (it != null) {
                    val latitude = "${it.latitude}"
                    val longitude = "${it.longitude}"
                    Log.d("MainFragment", "lattitude $latitude longitude $longitude")
                    Toast.makeText(context, "Lat:$latitude Long:$longitude", Toast.LENGTH_SHORT).show()

                    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2MjMzNjI1OTcsImV4cCI6MTYyMzM2NjE5N30.zyOiMF5qmQVjybDQTBju7e13KGiHv3AWgR1fOM9oUsE"
                    val id = "253"
                    val la = 20.176239271564697.toString()
                    val lo = (-99.32537744646021).toString()
                    getNotariesFromService(token, id, latitude, longitude)
                    //createMarker(latitude, longitude)
                } else {
                    Log.d("MainFragment", "null")

                }
            }
        } else {
            Log.d(TAG, "No hay permiso")
        }
    }

    private fun getNotariesFromService(token: String, id: String, lat: String, long: String) {
        val retrofit = MainActivity.getRetrofit()
        val call = retrofit.create(APIService::class.java)


        call.getNotaries(token, id, lat, long).enqueue(object : Callback<GetNotariesResponse> {
            override fun onResponse(
                    call: Call<GetNotariesResponse>,
                    response: Response<GetNotariesResponse>
            ) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Exito!")
                    val list = response.body()
                    if (list != null) {
                        listaNotarias.addAll(list.toMutableList())
                        createFragment()
                    }


                    Toast.makeText(context, "lista $list", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetNotariesResponse>, t: Throwable) {
                Log.i("bad", "ocurrio un error")
            }

        })

    }




    private fun createFragment() { //metodo que crea el fragment con el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment //obteniendo el fragment map del xml

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //cuando el mapa es creado lo guardamos en la variable map
        map = googleMap
        //cuando el mapa esta listo creamos el marcador
        //map.isMyLocationEnabled

        try {
            val notariItem = listaNotarias.first()
            val name = notariItem?.name
            val notariaLat = notariItem?.latitude
            val notariaLong = notariItem?.longitude

            Log.d(TAG, "onMapReady $notariItem")


            createMarker(notariaLat, notariaLong, name)
            //createMarker()
        }catch (e: Exception) {
            Log.d(TAG, "e:${e.message}")
        }



    }

    private fun createMarker(latitude: Double = 28.043893, longitude: Double = -16.539329, name: String = "notari") {
        // objeto para crear un bitmap a partir de un drawable
        val pickupMarkerDrawable = resources.getDrawable(R.drawable.ic_android,null)

        //cordenadas del punto para crear el marcador en el mapa
        val coordinates = LatLng(latitude, longitude) //recibe dos double
        val marker = MarkerOptions()
                .position(coordinates)
                .title(name)
                .icon(BitmapDescriptorFactory.fromBitmap(pickupMarkerDrawable.toBitmap(pickupMarkerDrawable.intrinsicWidth, pickupMarkerDrawable.intrinsicHeight, null)))
        map.addMarker(marker) //anadiendo el marcado

        //animar la camara con un zoom al marcador
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                400,
                null
        )
    }

    /*private fun createFragment() { //metodo que crea el fragment con el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment //obteniendo el fragment map del xml

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { //cuando el mapa es creado lo guardamos en la variable map
        map = googleMap
        //cuando el mapa esta listo creamos el marcador
        //map.isMyLocationEnabled

        //getcurrentLocation()

        //createMarker()
    }




    private fun createMarker(latitude: Double = 28.043893, longitude: Double = -16.539329) {
        // objeto para crear un bitmap a partir de un drawable
        val pickupMarkerDrawable = resources.getDrawable(R.drawable.ic_android,null)

        //cordenadas del punto para crear el marcador en el mapa
        val coordinates = LatLng(latitude, longitude) //recibe dos double
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
    }*/

    /*fun getcurrentLocation() {

        val task = fusedLocationProviderClient.lastLocation

        *//*if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }*//*

        task.addOnSuccessListener {
            if (it != null) {
                val latitude = it.latitude
                val longitude = it.longitude
                Log.i("MainFragment", "lattitude $latitude longitude $longitude")
                //createMarker(latitude, longitude)
            } else {
                Log.i("MainFragment", "null")

            }
        }



    }*/
}