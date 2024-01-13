package com.example.ifmapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.ifmapp.R

class ScannerScreen : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE =11
private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_screen)


     if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
         ==PackageManager.PERMISSION_GRANTED){
         startScanning()
     }else{
         ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
     }
    }

    private fun startScanning() {
        val scannerView:CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this,scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode  =AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {result->
            runOnUiThread {
                Toast.makeText(this, "scan Result $result", Toast.LENGTH_SHORT).show()
                var intent = Intent(this,CheckInScreen::class.java)
                intent.putExtra("qrResult",result.toString())
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                startScanning()
            }else{
                Toast.makeText(this, "permision declined", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized){
            codeScanner.releaseResources()
        }
    }

}