package com.example.camera2api

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.camera2api.databinding.ActivityDisplayImageBinding

class DisplayImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
//        val imageView: ImageView = findViewById(R.id.display_image)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imagePath = intent.getStringExtra("imagePath")
        if (imagePath != null) {
            Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(binding.displayImage)
        } else {
            // Handle the case where imagePath is null
        }

    }
}