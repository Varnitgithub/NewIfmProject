package com.example.ifmapp.animation

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

class AnimationClass {
    companion object{
          fun startBlinkAnimation(view:TextView) {
            val fadeIn = AlphaAnimation(0.0f, 1.0f)
            fadeIn.duration = 300 // You can adjust the duration as needed
            fadeIn.interpolator = DecelerateInterpolator()

            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.startOffset = 300 // Time to wait before starting fade out
            fadeOut.duration = 300 // You can adjust the duration as needed
            fadeOut.interpolator = DecelerateInterpolator()

            val animationSet = AnimationSet(true)
            animationSet.addAnimation(fadeIn)
            animationSet.addAnimation(fadeOut)

            // Set the AnimationListener to restart the animation when it ends
            animationSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    startBlinkAnimation(view)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            // Apply the animation to your layout
           view.startAnimation(animationSet)
          view.startAnimation(animationSet)
        }
    }
}