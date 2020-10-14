package com.example.audiovideosample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.audiovideosample.task1.Task1Activity
import com.example.audiovideosample.task2.Task2Activity
import com.example.audiovideosample.task3.Task3Activity
import com.example.audiovideosample.task4.Task4Activity
import com.example.audiovideosample.task5.Task5Activity
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
    }
}
