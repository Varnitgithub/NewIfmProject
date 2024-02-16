/*
package com.example.ifmapp.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ifmapp.R
import java.util.*

private const val CHECKOUT_CHANNEL_ID = "checkout_channel"
private const val CHECKOUT_ALARM_ACTION = "com.example.ifmapp.notification.CHECKOUT_ALARM"


class CheckoutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the checkout time event, show notification
        showNotification(context, "Checkout Time", "Please checkout now.")
    }
    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, title: String, message: String) {
        // Check if the app has the necessary permissions
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            // Create the notification channel
            createNotificationChannel(context)

            val notificationBuilder = NotificationCompat.Builder(context, CHECKOUT_CHANNEL_ID)
                .setSmallIcon(R.drawable.aadhar)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            // If notifications are not enabled, request permission
            requestNotificationPermission(context)
        }
    }

    private fun requestNotificationPermission(context: Context) {
        // Check if the app has permission to show notifications
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // Request notification permission
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            context.startActivity(intent)
        } else {
            // Handle the case where notification permission is not granted
            // You might want to show a different type of alert or guide the user to enable notifications
            // For simplicity, I'm not handling this case explicitly here
        }
    }

    fun setCheckoutAlarm(context: Context, checkoutTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(CHECKOUT_ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE // or use FLAG_MUTABLE if needed
        )

        // Set the alarm to trigger at the specified checkout time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, checkoutTime, pendingIntent)
    }

    // Call this function when the user checks in
    fun setCheckoutTime(context: Context, checkoutTime: Long) {
        // Save the checkout time in SharedPreferences or any other data storage method
        // For simplicity, I'm using SharedPreferences here
        val sharedPreferences = context.getSharedPreferences("CheckoutPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("checkoutTime", checkoutTime)
        editor.apply()

        // Set the alarm for checkout time
        setCheckoutAlarm(context, checkoutTime)
    }

    // Call this function when the app starts or when needed to check if it's time to show the notification
    fun checkCheckoutTime(context: Context) {
        val sharedPreferences = context.getSharedPreferences("CheckoutPrefs", Context.MODE_PRIVATE)
        val checkoutTime = sharedPreferences.getLong("checkoutTime", 0)

        if (checkoutTime != 0L && System.currentTimeMillis() >= checkoutTime) {
            // It's checkout time, show notification
            showNotification(context, "Checkout Time", "Please checkout now.")
        }
    }
}
*/
