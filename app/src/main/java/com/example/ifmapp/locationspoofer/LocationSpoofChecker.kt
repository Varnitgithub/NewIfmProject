import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log

class LocationSpoofChecker {

    companion object {
        fun isLocationSpoofed(context: Context): Boolean {
            return isMockLocationEnabled(context) || areThereFakeLocationApps(context)
        }

        private fun isMockLocationEnabled(context: Context): Boolean {
            try {
                // Check if mock locations are enabled in developer settings
                if (Settings.Secure.getInt(
                        context.contentResolver,
                        Settings.Secure.ALLOW_MOCK_LOCATION,
                        0
                    ) != 0
                ) {
                    Log.d("LocationSpoofChecker", "Mock locations are enabled in developer settings.")
                    return true
                }
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }

            return false
        }

        private fun areThereFakeLocationApps(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val allProviders = locationManager.allProviders
                for (provider in allProviders) {
                    try {
                        val location = locationManager.getLastKnownLocation(provider)
                        if (location != null && isMockLocation(location)) {
                            Log.d("LocationSpoofChecker", "Fake location app detected: $provider")
                            return true
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }

            return false
        }

        private fun isMockLocation(location: Location): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && location.isFromMockProvider
        }
    }
}
