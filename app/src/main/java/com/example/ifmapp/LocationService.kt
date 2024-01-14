import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ifmapp.Locationmodel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.greenrobot.eventbus.EventBus

class LocationServiceClass : Service() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private var NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"
    private var NOTIFICATION_ID = 1
    private var location: Location? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()


        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)

            }

        }
    }
@SuppressLint("MissingPermission")
fun createLocationRequest(){
    try {
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest!!,locationCallback!!,null)
    }catch (e:Exception){
e.printStackTrace()
    }
}

    private fun removeLocationUpdates(){
        locationCallback.let {
            if (it != null) {
                fusedLocationProviderClient?.removeLocationUpdates(it)
            }
        }
        stopForeground(true)
        stopSelf()

    }
    fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation

EventBus.getDefault().post(Locationmodel(location?.latitude,location?.longitude))

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

createLocationRequest()
        // Your background location tracking logic goes here
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }


}
