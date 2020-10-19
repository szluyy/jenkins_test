package com.example.audiovideosample.task8

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_8.*
import java.io.File

class Task8Activity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_8)
        btn1.setOnClickListener {

        }
    }

    fun extract(){
        var mediaExtractor=MediaExtractor()
        mediaExtractor.setDataSource(File(Environment.getExternalStorageDirectory(),"sample.mp4").absolutePath)
        for(index in 0 until mediaExtractor.trackCount){
            var formate=mediaExtractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
            if(formate?.startsWith("video")==true){
                mediaExtractor.selectTrack(index)
            }
        }

//       mediaExtractor.re

    }
}