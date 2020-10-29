package com.example.audiovideosample.task8

import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_8.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class Task8Activity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_8)
        btn1.setOnClickListener {
            extract()
        }
        var mediaPlayer=MediaPlayer()
    }

    fun extract(){
        var mediaExtractor=MediaExtractor()
        mediaExtractor.setDataSource(File(Environment.getExternalStorageDirectory(),"sample.mp4").absolutePath)
        var file=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"sample.h264")
        if(file.exists()) file.delete()
        var fos=FileOutputStream(file)
        for(index in 0 until mediaExtractor.trackCount){
            var formate=mediaExtractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
            if(formate?.startsWith("video")==true){
                mediaExtractor.selectTrack(index)
            }
        }
        var byteBuffer=ByteBuffer.allocate(1024*1024)
        var len=-1
        while (true){
            len= mediaExtractor.readSampleData(byteBuffer,0)
            if(len<=0) break
            Log.e("com.luyy","write to file ${len}")
            fos.write(byteBuffer.array(),0,len)
            fos.flush()
            mediaExtractor.advance()
        }
        fos.flush()
        mediaExtractor.release()
        fos.close()

//       mediaExtractor.re

    }
}