package com.example.camera2api

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.camera2api.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() ,CameraImagesAdapter.OnImageItemClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CameraImagesAdapter
    private lateinit var imageList: List<ImageModel>


    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Floating Button to add Data in dataBase
        binding.addImage.setOnClickListener {
            startActivity(Intent(this, CaptureImage::class.java))
        }

        if (checkPermission()) {
            loadImages()
        } else {
            requestPermission()
        }
    }
    override fun onItemClick(imagePath: String) {
        val intent = Intent(this, DisplayImageActivity::class.java)
        intent.putExtra("imagePath", imagePath)
        startActivity(intent)
    }
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImages()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadImages() {
        val imagePaths = getImagePaths().map { ImageModel(it) }
        imageList = imagePaths // Initialize imagelist with loaded image paths
        adapter = CameraImagesAdapter(this,imagePaths,this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter
        adapter.updateData(imagePaths)
    }

    private fun getImagePaths(): List<String> {
        val images = mutableListOf<String>()
//        var file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg")
        ///storage/emulated/0/Android/data/com.example.camera2api/files/Pictures/image.jpg
        val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "")
        if (directory.exists()) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile && (file.extension == "jpg" || file.extension == "png")) {
                        images.add(file.absolutePath)
                    }
                }
            }
        }
        return images
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle back button click
        onBackPressed()
        return true
    }
    override fun onResume() {
        super.onResume()
        loadImages()
    }

}