package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AcademySector(
    val id: String,
    val name: String,
    val codeName: String,
    val latitude: Double,
    val longitude: Double,
    val focusDescription: String,
    val chiefInstructor: String,
    val activeDrills: String
)

val ACADEMY_SECTORS = listOf(
    AcademySector(
        id = "SEC-ALPHA",
        name = "Tactical HQ & Central Kennels",
        codeName = "Sector Alpha",
        latitude = 34.0522,
        longitude = -118.2437,
        focusDescription = "Perimeter defense, high-drive protection, command briefing",
        chiefInstructor = "Master Sgt. K. Thorne",
        activeDrills = "Aggression Control & Decoy Engagement"
    ),
    AcademySector(
        id = "SEC-BRAVO",
        name = "Iron Ridge Protection Range",
        codeName = "Sector Bravo",
        latitude = 34.1012,
        longitude = -118.3253,
        focusDescription = "Open-field tactical obstacle courses, handler-shield drills",
        chiefInstructor = "Capt. Rebecca Stone",
        activeDrills = "Vehicle Extraction & Night Search"
    ),
    AcademySector(
        id = "SEC-CHARLIE",
        name = "Metro Scent & Detection Depot",
        codeName = "Sector Charlie",
        latitude = 33.9425,
        longitude = -118.4081,
        focusDescription = "Explosives, narcotics, human tracking scent discrimination",
        chiefInstructor = "Inspector M. Alvarez",
        activeDrills = "Blind Container & Cargo Scent Lineup"
    ),
    AcademySector(
        id = "SEC-DELTA",
        name = "Puppy Sentry Foundation Campus",
        codeName = "Sector Delta",
        latitude = 34.1808,
        longitude = -118.3090,
        focusDescription = "Early neural stimulation, bite grip foundation, socialization",
        chiefInstructor = "Dr. Laura Vance, DVM",
        activeDrills = "Prey Drive Shaping & Sound Desensitization"
    )
)

data class SectorProximity(
    val sector: AcademySector,
    val distanceMiles: Double,
    val bearingDegrees: Float
)

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Default reference location (Downtown Tactical Grid) if GPS is waiting or off
    val fallbackLatitude = 34.0522
    val fallbackLongitude = -118.2437

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(
        onLocationReceived: (latitude: Double, longitude: Double) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    // Fall back to last known location or tactical default
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            onLocationReceived(lastLoc.latitude, lastLoc.longitude)
                        } else {
                            onLocationReceived(fallbackLatitude, fallbackLongitude)
                        }
                    }.addOnFailureListener {
                        onLocationReceived(fallbackLatitude, fallbackLongitude)
                    }
                }
            }.addOnFailureListener { e ->
                onError(e.message ?: "Failed to get location")
                onLocationReceived(fallbackLatitude, fallbackLongitude)
            }
        } catch (e: SecurityException) {
            onError("Location permission not granted. Showing tactical grid simulation.")
            onLocationReceived(fallbackLatitude, fallbackLongitude)
        }
    }

    companion object {
        fun calculateDistanceMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val earthRadiusMiles = 3958.8
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return earthRadiusMiles * c
        }

        fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val phi1 = Math.toRadians(lat1)
            val phi2 = Math.toRadians(lat2)
            val deltaLambda = Math.toRadians(lon2 - lon1)

            val y = sin(deltaLambda) * cos(phi2)
            val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
            val initialBearing = Math.toDegrees(atan2(y, x))
            return ((initialBearing + 360) % 360).toFloat()
        }
    }
}
