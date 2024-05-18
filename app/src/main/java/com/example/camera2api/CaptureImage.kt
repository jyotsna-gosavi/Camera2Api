package com.example.camera2api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.camera2api.databinding.ActivityCaptureImageBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class CaptureImage : AppCompatActivity() {

    lateinit var binding: ActivityCaptureImageBinding
    lateinit var capReq: CaptureRequest.Builder
    lateinit var handlerThread: HandlerThread
    lateinit var handler: Handler
    lateinit var cameraDevice: CameraDevice
    lateinit var cameraCaptureSession: CameraCaptureSession
    lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    lateinit var imageReader: ImageReader
    lateinit var captureResult: CaptureResult

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCaptureImageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        textureView = binding.textureView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getPermission()

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }
        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader?.acquireLatestImage()
            var buffer = image!!.planes[0].buffer
            var bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("ddMMyyyy_HH:mm:ss")
            val currentDateAndTime: String = dateFormat.format(calendar.time)
            val fileName="IMG_"+currentDateAndTime
            var file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$fileName.jpg")
            var opStream = FileOutputStream(file)

            opStream.write(bytes)

            opStream.close()
            image.close()
            Toast.makeText(this@CaptureImage,"Image Captured",Toast.LENGTH_LONG).show()
        }, handler)

        binding.btnTake.setOnClickListener {
            capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            capReq.addTarget(imageReader.surface)
            cameraCaptureSession.capture(capReq.build(),null,null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice.close()
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }

    @SuppressLint("MissingPermission")
    fun openCamera() {

        cameraManager.openCamera(
            cameraManager.cameraIdList[0], object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    capReq = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    var surface = Surface(textureView.surfaceTexture)
                    capReq.addTarget(surface)
                    cameraDevice.createCaptureSession(
                        listOf(surface,imageReader.surface),
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                cameraCaptureSession = session
                                cameraCaptureSession.setRepeatingRequest(capReq.build(), null, null)
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {

                            }
                        },
                        handler
                    )
                }

                override fun onDisconnected(camera: CameraDevice) {

                }

                override fun onError(camera: CameraDevice, error: Int) {

                }
            },
            handler
        )
    }

    private fun getPermission() {
        var permissionList = mutableListOf<String>()

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionList.add(
            android.Manifest.permission.CAMERA
        )
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionList.add(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionList.add(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionList.size > 0) {
            requestPermissions(permissionList.toTypedArray(), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                getPermission()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle back button click
        onBackPressed()
        finish()
        return true
    }
}