package com.example.audiovideosample

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.audiovideosample.task1.Task1Activity
import com.example.audiovideosample.task10.Task10Activity
import com.example.audiovideosample.task12.Task12Activity
import com.example.audiovideosample.task2.Task2Activity
import com.example.audiovideosample.task3.Task3Activity
import com.example.audiovideosample.task4.Task4Activity
import com.example.audiovideosample.task5.Task5Activity
import com.example.audiovideosample.task7.Task7Activity
import com.example.audiovideosample.task8.Task8Activity
import com.example.audiovideosample.task9.RecordActivity
import com.example.audiovideosample.task9.Task9Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn1.setOnClickListener {
            var intent=Intent(this,Task1Activity::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            var intent=Intent(this, Task2Activity::class.java)
            startActivity(intent)
        }

        btn3.setOnClickListener {
            var intent=Intent(this, Task3Activity::class.java)
            startActivity(intent)
        }

        btn4.setOnClickListener {
            var intent=Intent(this, Task4Activity::class.java)
            startActivity(intent)
        }

        btn5.setOnClickListener {
            var intent=Intent(this, Task5Activity::class.java)
            startActivity(intent)
        }

        btn7.setOnClickListener {
            var intent=Intent(this, Task7Activity::class.java)
            startActivity(intent)
        }

        btn8.setOnClickListener {
            var intent=Intent(this, Task8Activity::class.java)
            startActivity(intent)
        }

        btn9.setOnClickListener {
//            var intent=Intent(this, Task9Activity::class.java)
            var intent=Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
        
        btn10.setOnClickListener {
            var intent=Intent(this, Task10Activity::class.java)
            startActivity(intent)
        }

        btn12.setOnClickListener {
            var intent=Intent(this, Task12Activity::class.java)
            startActivity(intent)
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO),1)
    }
}
