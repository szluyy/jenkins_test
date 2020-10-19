package com.example.audiovideosample.task2

import android.content.pm.PackageInfo
import android.media.*
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioTrack.MODE_STREAM
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Permissions
import java.util.jar.Manifest
import kotlin.math.min

/**
 * PCM数据采集与播放
 */
class Task2Activity :AppCompatActivity(){
    val sampleRateInHz=44100
    val chanelConfig=AudioFormat.CHANNEL_IN_MONO
    val audioFormat=AudioFormat.ENCODING_PCM_16BIT
    var isRecoding=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_2)
        btn_record.setOnClickListener {
            if(!isRecoding){
                GlobalScope.launch {
                    startRecord()
                }
            }else{
                isRecoding=false
            }
        }

        btn_play.setOnClickListener{
           GlobalScope.launch {
               play()
           }
        }

      ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.RECORD_AUDIO),1)

    }

    suspend fun startRecord(){
        withContext(Dispatchers.IO){
            val minBufferSize=  AudioRecord.getMinBufferSize(sampleRateInHz,chanelConfig,audioFormat)
            val audioRecord=   AudioRecord(MediaRecorder.AudioSource.MIC,sampleRateInHz,chanelConfig,audioFormat,minBufferSize)
            audioRecord.startRecording()
            isRecoding=true
            var file=File(cacheDir,"hello.pcm")
            var fileOutputStream =FileOutputStream(file)
            var buffer= ByteArray(minBufferSize)
            while (isRecoding){
                audioRecord.read(buffer,0,minBufferSize)
                fileOutputStream.write(buffer)
                fileOutputStream.flush()
            }
            audioRecord.stop()
            fileOutputStream.flush()
            fileOutputStream.close()

        }
    }

    suspend fun play(){
        withContext(Dispatchers.IO) {
            val minBufferSize =
                AudioRecord.getMinBufferSize(sampleRateInHz, chanelConfig, audioFormat)
            var track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRateInHz,
                CHANNEL_OUT_MONO,
                audioFormat,
                minBufferSize,
                MODE_STREAM
            )
            track.play()
            var file = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "first.pcm")
            var fis = FileInputStream(file)
            var buffer = ByteArray(minBufferSize)
            var len = -1
            do {
                len = fis.read(buffer)
                if (len <= 0) break
                track.write(buffer, 0, len)

            } while (true)
        }
    }

}