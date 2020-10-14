package com.example.audiovideosample.task4

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_4.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

class Task4Activity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_4)
        btn1.setOnClickListener {
            GlobalScope.launch {
                getMediaInfo()
            }
        }
    }

    suspend fun getMediaInfo(){
        withContext(Dispatchers.IO){
            var mediaExtractor=MediaExtractor()
            mediaExtractor.setDataSource("https://knd-vita.obs.cn-east-3.myhuaweicloud.com/pro/video/1599641959485.mp4?AccessKeyId=XXAQGOL8KYHZVJJJ6E92&Expires=1760209200&Signature=zvKS6%2Bqb%2FeYD%2BjG0ltBgvN9ucyI%3D")
            var count= mediaExtractor.trackCount

            for (index in 0 until count){
                var format= mediaExtractor.getTrackFormat(index)
                val mime =format.getString(MediaFormat.KEY_MIME)
                if("audio/mp4a-latm"==mime){
                    mediaExtractor.selectTrack(index)
                }
                Log.e("com.luyy","mime==="+mime)
            }
            var data=ByteBuffer.allocate(1024)
            var len=-1
            do {
               len= mediaExtractor.readSampleData(data,0)
                if(len<=0) break

            }while (true)
        }
    }
}