package com.vanitap.myfamilysafety

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var cameraPreview: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        cameraPreview = view.findViewById(R.id.camera_preview)
        surfaceHolder = cameraPreview.holder
        surfaceHolder.addCallback(this)

        return view
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopCamera()
    }

    private fun startCamera() {
        try {
            camera = Camera.open()
            camera?.setDisplayOrientation(90) // Adjust orientation if necessary
            camera?.setPreviewDisplay(surfaceHolder)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e("CameraFragment", "Error setting camera preview: ${e.message}")
        }
    }

    private fun stopCamera() {
        camera?.apply {
            stopPreview()
            setPreviewCallback(null)
            release()
        }
        camera = null
    }

    private fun captureImage() {
        camera?.takePicture(null, null, pictureCallback)
    }

    private val pictureCallback = Camera.PictureCallback { data, _ ->
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"
            val fos = requireContext().openFileOutput(fileName, 0)
            fos.write(data)
            fos.close()
            Log.d("CameraFragment", "Image saved: $fileName")
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error saving image: ${e.message}")
        } finally {
            camera?.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startCamera()
            } else {
                Log.e("CameraFragment", "Camera permission denied")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    fun takePicture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            captureImage()
        } else {
            Log.e("CameraFragment", "Camera permission not granted")
            // Handle case where permission is not granted
        }
    }
}
