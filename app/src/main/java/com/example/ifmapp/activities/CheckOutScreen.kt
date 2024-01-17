package com.example.ifmapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityCheckOutScreenBinding
import com.example.ifmapp.fragments.BottomFragment

class CheckOutScreen : AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetectorCompat

    private lateinit var binding: ActivityCheckOutScreenBinding
    private val bottomFragment by lazy { BottomFragment() }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_check_out_screen)
binding = DataBindingUtil.setContentView(this,R.layout.activity_check_out_screen)
        /*gestureDetector = GestureDetectorCompat(this, SwipeGestureListener())

            binding.frameLayout.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }*/
binding.checkInBtn.isEnabled = false
        val bottomFragment = BottomFragment.newInstance()
        bottomFragment.show(supportFragmentManager, "add_photo_dialog_fragment")

        binding.checkoutBtn.setOnClickListener {
            startActivity(Intent(this,ScannerScreen::class.java))
        }

    }

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val deltaY = e2.y.minus(e1?.y ?: 0F) ?: 0F

            // Check if the swipe is from bottom to top
            if (deltaY < 0) {
                // Show the bottom sheet fragment
                val bottomFragment = BottomFragment.newInstance()
                bottomFragment.show(supportFragmentManager, "bottom_fragment")
            }

            // Return true to indicate that the event has been consumed
            return true
        }
    }

}