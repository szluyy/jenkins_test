package com.example.audiovideosample.task3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.audiovideosample.R

class Task3Activity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_3)
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),1)

    }
}