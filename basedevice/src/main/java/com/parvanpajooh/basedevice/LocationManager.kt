package com.parvanpajooh.basedevice

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Looper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.parvanpajooh.basedomain.models.response.LocationRes
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class LocationManager(context: Context) {
    private val googleApiClient: GoogleApiClient =
        GoogleApiClient.Builder(context).addApi(LocationServices.API).build()

    init {
        googleApiClient.connect()
    }


    suspend fun requestGPSSettings(activity: Activity): Result<LocationRes> {
        val deferred = CompletableDeferred<Result<LocationRes>>()
        val locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(activity) { getLocation(activity, deferred) }
        task.addOnFailureListener(activity) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        activity,
                        GPS_REQUEST
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                    deferred.complete(Result.Error("location null", ErrorCode.UNKNOWN))
                }

            }
        }
        return deferred.await()
    }

    fun requestGPSSettings(activity: Activity, onLocationCallback: LocationCallback) {
        val locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(activity) { getLocation(activity, onLocationCallback) }
        task.addOnFailureListener(activity) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        activity,
                        GPS_REQUEST
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }

            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10
        mLocationRequest.fastestInterval = 10
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(activity: Activity, onLocationCallback: LocationCallback) {
        val providerClient = LocationServices.getFusedLocationProviderClient(activity)
        providerClient.requestLocationUpdates(
            createLocationRequest(),
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        onLocationCallback.onSuccess(location.latitude, location.longitude)
                        providerClient.removeLocationUpdates(this)
                    }
                }
            },
            null
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(
        activity: Activity,
        deferred: CompletableDeferred<Result<LocationRes>>
    ) {
        val providerClient = LocationServices.getFusedLocationProviderClient(activity)
        providerClient.requestLocationUpdates(
            createLocationRequest(),
            object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val lastLocation = result.lastLocation
                    if (lastLocation != null) {
                        providerClient.removeLocationUpdates(this)
                        if (lastLocation.isFromMockProvider) {
                            deferred.complete(
                                Result.Error(
                                    "نقطه مکانی شما غیر واقعی است.",
                                    ErrorCode.UNKNOWN
                                )
                            )
                        } else {
                            deferred.complete(
                                Result.Success(
                                    LocationRes(
                                        lastLocation.latitude,
                                        lastLocation.longitude
                                    )
                                )
                            )
                        }
                    } else {
                        deferred.complete(
                            Result.Error(
                                "پاسخی از GPS دریافت نشد.\nدوباره تلاش کنید.",
                                ErrorCode.UNKNOWN
                            )
                        )
                    }
                }
            }, Looper.myLooper()
        )
    }

    interface LocationCallback {
        fun onSuccess(lat: Double, lng: Double)
    }

    val GPS_REQUEST = 0x1
}
