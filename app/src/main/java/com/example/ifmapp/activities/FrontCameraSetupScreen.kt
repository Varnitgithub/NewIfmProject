package com.example.ifmapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.example.ifmapp.R
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.File
class FrontCameraSetupScreen : AppCompatActivity() {

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var clickedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_camera_setup_screen)

        previewView = findViewById(R.id.previewView)
        clickedImage = findViewById(R.id.clickedImage)
        clickedImage.visibility = View.GONE
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Create a preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Set up the image capture use case
            imageCapture = ImageCapture.Builder().build()

            // Set up the camera selector to use the front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Set up tap gesture detection
            val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    captureImage()
                    return true
                }
            })

            previewView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

            try {
                // Unbind any existing camera use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the use cases to the camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val imageCapture = imageCapture

        // Create a file to save the image
        val outputOptions = ImageCapture.OutputFileOptions.Builder(createTempFile()).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Process the saved image
                    val savedUri = outputFileResults.savedUri
                    val savedFile = File(savedUri?.toFile()?.path ?: "")

                    // Ensure that the file exists before attempting to decode it
                    if (savedFile.exists()) {
                        val imageBitmap = BitmapFactory.decodeFile(savedFile.absolutePath)
                        val imageString = convertBitmapToString(imageBitmap)
//previewView.visibility = View.GONE
//                        clickedImage.visibility = View.VISIBLE
//
//                        clickedImage.setImageURI(Uri.parse(imageString))
                        Log.d("TAGGGGGGGG", "onImageSaved: this is the clicked image ${imageString.length}")

                    val intent = Intent(this@FrontCameraSetupScreen,CheckInScreen::class.java)
                      //  intent.putExtra("bitmapImage",imageBitmap)
                        intent.putExtra("stringImage",imageString)
                        startActivity(intent)

                    // Now 'imageString' contains the string representation of the image
                    } else {
                        Toast.makeText(applicationContext, "Image file not found", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "Image capture failed", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Function to convert Bitmap to String (you may need to modify this part)
    private fun convertBitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
