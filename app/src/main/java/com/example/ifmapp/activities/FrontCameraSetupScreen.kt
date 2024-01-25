package com.example.ifmapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityFrontCameraSetupScreenBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FrontCameraSetupScreen : AppCompatActivity() {
    private lateinit var binding: ActivityFrontCameraSetupScreenBinding
    private lateinit var imageCapture: ImageCapture
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var photoFile: File
    private val sharedPrefFile = "com.example.myapp.PREFERENCE_FILE_KEY"
    private lateinit var sharedPref: SharedPreferences
    private var base64Image: String? = null
    private var rotatedBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageCapture = ImageCapture.Builder().build()
        sharedPref = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        //setContentView(R.layout.activity_front_camera_setup_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_front_camera_setup_screen)
        binding.okBtn.visibility = View.GONE

        startCamera()
        binding.previewView.setOnClickListener {
            takePhoto()
        }


        binding.okBtn.setOnClickListener {
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Create a preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Set up the image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Set up the camera selector to use the front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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

    private fun takePhoto() {
        // Create a timestamped file to save the image
        val photoFileName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
        val storageDirectory = getExternalFilesDir(null)
        photoFile = File.createTempFile(photoFileName, ".jpg", storageDirectory)

        // Create output options object to configure ImageCapture
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture the image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image is saved, now process the image
                    processImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "Error capturing image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    private fun processImage(photoFile: File) {

        binding.previewView.visibility = View.GONE
        binding.imageview.visibility = View.VISIBLE
        base64Image = encodeImageToBase64(photoFile)

        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

        val bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        val exif = ExifInterface(photoFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
         rotatedBitmap = rotateBitmap(bitmap, orientation)

        binding.imageview.setImageBitmap(rotatedBitmap)
      //  Glide.with(this).load(base64Image).into(binding.imageview)
        val editor = sharedPref.edit()
        editor.putString("photoInStringUrl", base64Image)


        editor.apply()
       val base64image = bitmapToBase64(rotatedBitmap!!)

        val resultIntent = Intent()
        resultIntent.putExtra("capturedBitmap", base64image)
        setResult(Activity.RESULT_OK, resultIntent)

        // Close CameraActivity
        finish()
    }

    private fun encodeImageToBase64(imageFile: File): String {
        val bytes = imageFile.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
