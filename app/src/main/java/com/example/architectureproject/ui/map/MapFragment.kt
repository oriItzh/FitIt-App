package com.example.architectureproject.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.architectureproject.R
import com.example.architectureproject.databinding.MapLayoutBinding
import com.example.architectureproject.ui.viewModels.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.Scanner

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: MapLayoutBinding? = null

    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var gMap: GoogleMap
    private lateinit var pMap: GoogleMap
    private lateinit var gMapView: MapView
    private lateinit var pMapView: MapView
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapLayoutBinding.inflate(inflater, container, false)

        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel.address.observe(viewLifecycleOwner, Observer { address ->
            binding.location.text = address
        })

        // Gym radius

        binding.plusGym.setOnClickListener {
            var gymR = binding.radiusGym.text.toString().toDouble()
            // Increase
            if (gymR < 10) {
                gymR += 0.5
                binding.radiusGym.text = gymR.toString()
                locationViewModel.setRadiusGym(gymR)
            }
        }

        binding.minusGym.setOnClickListener {
            var gymR = binding.radiusGym.text.toString().toDouble()
            // Decrease
            if (gymR >= 0.5) {
                gymR -= 0.5
                binding.radiusGym.text = gymR.toString()
                locationViewModel.setRadiusGym(gymR)
            }
        }

        // Park radius

        binding.plusPark.setOnClickListener {
            var parkR = binding.radiusPark.text.toString().toDouble()
            // Increase
            if (parkR < 10) {
                parkR += 0.5
                binding.radiusPark.text = parkR.toString()
                locationViewModel.setRadiusPark(parkR)

            }
        }

        binding.minusPark.setOnClickListener {
            var parkR = binding.radiusPark.text.toString().toDouble()
            // Decrease
            if (parkR >= 0.5) {
                parkR -= 0.5
                binding.radiusPark.text = parkR.toString()
                locationViewModel.setRadiusPark(parkR)
            }
        }

        locationViewModel.radiusGym.observe(viewLifecycleOwner){
            if (it == null) {
                binding.radiusGym.text = "0"
            } else {
                binding.radiusGym.text = it.toString()
            }
        }

        locationViewModel.radiusPark.observe(viewLifecycleOwner){
            if (it == null) {
                binding.radiusPark.text = "0"
            } else {
                binding.radiusPark.text = it.toString()
            }
        }


        gMapView = binding.root.findViewById(R.id.mapGym)
        gMapView.onCreate(savedInstanceState)
        gMapView.getMapAsync(this)

        pMapView = binding.root.findViewById(R.id.mapPark)
        pMapView.onCreate(savedInstanceState)
        pMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (!::gMap.isInitialized) {
            gMap = googleMap
            checkLocationPermission()
        } else if (!::pMap.isInitialized) {
            pMap = googleMap
            checkLocationPermissionForParks()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            gMap.isMyLocationEnabled = true
            getDeviceLocation("gym")
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkLocationPermissionForParks() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            pMap.isMyLocationEnabled = true
            getDeviceLocation("park")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation(type: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            try {
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLocation = LatLng(location.latitude, location.longitude)
                            if (type == "gym") {
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                                locationViewModel.radiusGym.observe(viewLifecycleOwner) {
                                    var radius = 0.0
                                    if (it != null){
                                        radius = it * 1000
                                    }

                                    findClosest(currentLocation, "gym", radius.toInt())
                                }
                            } else {
                                pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                                locationViewModel.radiusPark.observe(viewLifecycleOwner) {
                                    var radius = 0.0
                                    if (it != null){
                                        radius = it * 1000
                                    }
                                    findClosest(currentLocation, "park", radius.toInt())
                                }
                            }
                        }
                    }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun findClosest(location: LatLng, type: String, r: Int) {
        val apiKey = getString(R.string.google_maps_key)
        val locationString = "${location.latitude},${location.longitude}"
        val radius = r
        val language = Locale.getDefault().language

        val urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=$locationString" +
                "&radius=$radius" +
                "&type=$type" +
                "&key=$apiKey" +
                "&language=$language"

        DestinationsAround(type, location, radius).execute(urlString)
    }

    private inner class DestinationsAround(private val type: String, private val currentLocation: LatLng, private val radius: Int) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String?): String {
            val urlConnection: HttpURLConnection?
            return try {
                val url = URL(urls[0])
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().language)
                val inputStream = urlConnection.inputStream
                val scanner = Scanner(inputStream).useDelimiter("\\A")
                if (scanner.hasNext()) scanner.next() else ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!result.isNullOrEmpty()) {
                val jsonObject = JSONObject(result)
                val resultsArray = jsonObject.getJSONArray("results")
                var locationDest: LatLng? = null

                for (i in 0 until resultsArray.length()) {
                    val dest = resultsArray.getJSONObject(i)
                    val lat =
                        dest.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    val lng =
                        dest.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    val name = dest.getString("name")
                    val address = dest.getString("vicinity")
                    locationDest = LatLng(lat, lng)

                    if (locationDest != null) {
                        addMarker(
                            locationDest,
                            name,
                            BitmapDescriptorFactory.HUE_RED,
                            type
                        )
                    }
                }

                val zoom = calculateZoomLevel(radius)

                if (type == "gym")
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
                else
                    pMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))

                if (locationDest == null)
                    Toast.makeText(requireContext(), getString(R.string.increase_range), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_searching),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun calculateZoomLevel(radius: Int): Float {
            return (16 - Math.log(radius.toDouble() / 500) / Math.log(2.0)).toFloat()
        }
    }

    private fun addMarker(location: LatLng, title: String?, color: Float, type: String) {
        val markerOptions = MarkerOptions().position(location).title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(color))
        if (type == "gym") {
            gMap.addMarker(markerOptions)
        } else {
            pMap.addMarker(markerOptions)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    gMap.isMyLocationEnabled = true
                    getDeviceLocation("gym")
                    pMap.isMyLocationEnabled = true
                    getDeviceLocation("park")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        gMapView.onResume()
        pMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        gMapView.onPause()
        pMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gMapView.onDestroy()
        pMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        gMapView.onLowMemory()
        pMapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}


