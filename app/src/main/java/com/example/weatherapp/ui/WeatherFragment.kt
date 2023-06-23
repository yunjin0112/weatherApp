package com.example.weatherapp.ui

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.*

class WeatherFragment : Fragment() {

    private val binding: FragmentWeatherBinding by lazy {
        FragmentWeatherBinding.inflate(layoutInflater)
    }

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = weatherViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
        }

        // When there is the city searched last, render the the weather for city last searched
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("weatherSearch",
            Context.MODE_PRIVATE)
        val latestQuery = sharedPreferences.getString("lastSearch", "")
        if (latestQuery!!.isNotEmpty()) {
            weatherViewModel.getWeatherDataForCity(latestQuery.toString())
            alreadyShown = true
        }

        // Render the weather info of current loaction when the user allows the location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            locationAlertAnswered = true
        }
    }

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // This is for debugging. check if the callback is called
            Toast.makeText(
                    requireContext(),
                    "Callback is called",
                    Toast.LENGTH_LONG
                )
                    .show()

            val locationList = locationResult.locations
            var isSeachSuccessful = weatherViewModel.isSeachSuccessful
            var buttonClicked = weatherViewModel.buttonCliked
            // When the user
            if (!alreadyShown && locationAlertAnswered && locationList.isNotEmpty() && isSeachSuccessful && !buttonClicked) {
                //The last location in the list is the newest
                val location = locationList.last()
                weatherViewModel.onLocationUpdated (location)
                alreadyShown = true
                locationAlertAnswered = false
            }
            // Save the last searched city
            saveLatestQuery(isSeachSuccessful)
        }
    }

    // Save the valid query when the query input is a valid city
   fun saveLatestQuery(isSeachSuccessful: Boolean)  {
        // This is for debugging.
        Toast.makeText(
            requireContext(),
            "saveLatestQuery",
            Toast.LENGTH_LONG
        )
            .show()

        // Save the city name to sharedPreferences when the valid city is given,
        if (isSeachSuccessful) {
            var latestQuery = weatherViewModel.getQuery(binding.queryEditText.editableText)
            Toast.makeText(
                requireContext(),
                "last query: " + latestQuery.toString(),
                Toast.LENGTH_LONG
            )
                .show()
            if (latestQuery.length > 0) {
                val sharedPreferences: SharedPreferences =
                    requireContext().getSharedPreferences(
                        "weatherSearch",
                        Context.MODE_PRIVATE
                    )
                val myEdit = sharedPreferences.edit()
                myEdit.putString("lastSearch", latestQuery)
                myEdit.commit()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        // Render the weather info of current loaction when the user allows the location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            locationAlertAnswered = true
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Show the explanation for the current location request
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show the alert dialog which includes the explanation of the current location request
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                        alreadyShown = false
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
        private var alreadyShown = false
        private var locationAlertAnswered = false
    }
}
