package com.giniapps.weatherapplication.ui.main

import android.content.*
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.giniapps.weatherapplication.permission.Permission
import com.giniapps.weatherapplication.permission.PermissionManager
import com.giniapps.weatherapplication.R
import com.giniapps.weatherapplication.databinding.FragmentMainBinding
import com.giniapps.weatherapplication.model.Weather
import com.giniapps.weatherapplication.service.LocationService
import com.giniapps.weatherapplication.utils.toText
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    private var foregroundOnlyLocationServiceBound = false

    private var locationService: LocationService? = null
    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var permissionManager: PermissionManager

    private var locationFlow: Job? = null

    private val gpsReceiver = object : BroadcastReceiver() {
         var isGpsEnabled: Boolean = false
        private var isNetworkEnabled: Boolean = false

        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let { act ->
                if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
                    val locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    if (!(isGpsEnabled || isNetworkEnabled)) {
                        Snackbar.make(
                            binding.main,
                            getString(R.string.turnGps),
                            Snackbar.LENGTH_LONG

                        ).show()

                    }
                }
            }
        }
    }

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        permissionManager.setFragment(this)
        sharedPreferences =
            requireContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionManager
            // Check all permissions without bundling them
            .request(Permission.Location)
            .rationale("We want permission for Location (Fine+Coarse)")
            .checkDetailedPermission { result ->
                if (result.all { it.value }) {
                    subscribeToLocationUpdates()
                    lifecycleScope.launchWhenStarted {
                        mainViewModel.weatherUIState.collect {
                            when (it) {
                                is WeatherUiState.Success -> {
                                    updateUi(it.currentWeather)
                                    binding.progressBar.isVisible = false
                                }
                                is WeatherUiState.Error -> {
                                    updateUi(it.currentWeather)

                                    Snackbar.make(binding.main, it.message, Snackbar.LENGTH_LONG)
                                        .show()
                                    binding.progressBar.isVisible = false

                                }
                                is WeatherUiState.Loading -> {
                                    binding.progressBar.isVisible = true
                                }


                                else -> {}
                            }
                        }
                    }
                }
            }

    }


    private fun updateUi(it: Weather?) {
        it?.let {
            binding.apply {
                degreesTv.text =getString(R.string.degrees,it.temperature.roundToInt())
                Glide.with(image)
                    .load(Uri.parse(it.getImageUrl()))
                    .fitCenter()
                    .into(image)
                descriptionTv.text = it.summary
                addressTv.text = it.address
            }
        }

    }


    private fun subscribeToLocationUpdates() {
        locationFlow = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.getLocation().onEach {
                    Log.d("TAG", "subscribeToLocationUpdates: Location: ${it.toText()}")
                }.collectLatest {
                    mainViewModel.getCurrentWeatherByLocation(it.latitude, it.longitude)
                }
            }
        }

        locationService?.subscribeToLocationUpdates()
    }

    private fun unsubscribeToLocationUpdates() {
        locationFlow?.cancel()
        locationService?.unsubscribeToLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        requireContext().registerReceiver(gpsReceiver, filter)

        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        requireContext().bindService(
            serviceIntent,
            foregroundOnlyServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }


    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
          //  requireContext().unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        requireContext().unregisterReceiver(gpsReceiver)
        unsubscribeToLocationUpdates()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}