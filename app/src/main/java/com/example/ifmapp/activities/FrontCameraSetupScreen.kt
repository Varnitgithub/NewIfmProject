package com.example.lenovo.sam.CheckInOut

import android.content.Context
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.hardware.Camera.ShutterCallback
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.ifmapp.R

class FrontCameraSetupScreen : AppCompatActivity() {
    private var mCam: Camera? = null
    private var rawCallback: PictureCallback? = null
    private var shutterCallback: ShutterCallback? = null
    private var jpegCallback: PictureCallback? = null
    var fl: FrameLayout? = null
    var frnt_facing_cam_id = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_camera_setup_screen)
       // fl = findViewById<View>(R.id.camPreview) as FrameLayout
        openFrontFacingCameraGingerbread()
        callBacks()
        addCamera()
        fl!!.setOnClickListener { mCam!!.takePicture(shutterCallback, rawCallback, jpegCallback) }
    }

    private fun openFrontFacingCameraGingerbread() {
        try {
            var cameraCount = 0
            //            CameraManager manager = (CameraManager) CameraActivity.this.getSystemService(Context.CAMERA_SERVICE);

//            Camera cam = null;
            val cameraInfo = Camera.CameraInfo()
            cameraCount = Camera.getNumberOfCameras()
            println("Val of Get al list of cameras $cameraCount")
            for (camIdx in 0 until cameraCount) {
                Camera.getCameraInfo(camIdx, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frnt_facing_cam_id = camIdx
                    println("Val of Front facing has num $frnt_facing_cam_id out of total num of cameras are $cameraCount")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCameraInLayout(layout: FrameLayout?, cameraId: Int) {
//        System.out.println("Val of Camera id to open is "+cameraId);
        mCam = Camera.open(cameraId)
        if (mCam != null) {
            val mCamPreview: MirrorView = MirrorView(this, mCam!!)
            layout!!.addView(mCamPreview)
        }
    }

    fun callBacks() {
        rawCallback =
            PictureCallback { data, camera -> Log.d("Log", "onPictureTaken - raw") }
        shutterCallback = ShutterCallback { }
        jpegCallback = PictureCallback { data, camera ->
//            Cm.image_checkIn_CheckOut = null
//            //                BitmapFactory.Options options = new BitmapFactory.Options();
//            //                options.inMutable = true;
//            //                Cm.image_checkIn_CheckOut = BitmapFactory.decodeByteArray(data, 0, data.length);
//            val arrayInputStream = ByteArrayInputStream(data)
//            Cm.image_checkIn_CheckOut = BitmapFactory.decodeStream(arrayInputStream)

            /*
                    Matrix matrixMirror = new Matrix();
                    matrixMirror.preScale(-1.0f, 1.0f);
                    Cm.image_checkIn_CheckOut = Bitmap.createBitmap(
                            Cm.image_checkIn_CheckOut,
                            0,
                            0,
                            Cm.image_checkIn_CheckOut.getWidth(),
                            Cm.image_checkIn_CheckOut.getHeight(),
                            matrixMirror,
                            false);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Cm.image_checkIn_CheckOut;
                    Cm.image_checkIn_CheckOut = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);*/
            val matrixMirror = Matrix()
            matrixMirror.postRotate(90f)
            matrixMirror.preScale(-1.0f, 1.0f)
            /*Cm.image_checkIn_CheckOut = Bitmap.createBitmap(
                Cm.image_checkIn_CheckOut,
                0,
                0,
                Cm.image_checkIn_CheckOut.getWidth(),
                Cm.image_checkIn_CheckOut.getHeight(),
                matrixMirror,
                false*/
            //)
            try {
                mCam!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                finish()
            }
        }
    }

    fun addCamera() {
        startCameraInLayout(fl, frnt_facing_cam_id)
        setCameraDisplayOrientationAndSize()
    }

    fun setCameraDisplayOrientationAndSize() {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(frnt_facing_cam_id, info)
        val rotation = windowManager.defaultDisplay.rotation
        val degrees = rotation * 90
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        mCam!!.setDisplayOrientation(result)
        val previewSize = mCam!!.parameters.previewSize
      /*  if (result == 90 || result == 270) {
            mHolder.setFixedSize(previewSize.height, previewSize.width)
        } else {
            mHolder.setFixedSize(previewSize.width, previewSize.height)
        }*/
    }

    inner class MirrorView(context: Context?, private val mCamera: Camera) :
        SurfaceView(context), SurfaceHolder.Callback {
        private val TAG = "Shobhit"
        private val rawCallback: PictureCallback? = null
        private val shutterCallback: ShutterCallback? = null
        private val jpegCallback: PictureCallback? = null

       /* init {
            mHolder = holder
            mHolder.addCallback(this)
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
*/
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                mCamera.setPreviewDisplay(holder)
                mCamera.startPreview()
            } catch (error: Exception) {
                Log.d(
                    TAG,
                    "Error starting mPreviewLayout: " + error.message
                )
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int, w: Int,
            h: Int
        ) {
           /* if (mHolder.surface == null) {
                return
            }*/
            // can't make changes while mPreviewLayout is active
            try {
                mCamera.stopPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                // start up the mPreviewLayout
             //   mCamera.setPreviewDisplay(mHolder)
                mCamera.startPreview()
            } catch (error: Exception) {
                Log.d(TAG, "Error starting mPreviewLayout: " + error.message)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            mCam!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
