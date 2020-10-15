package com.example.audiovideosample.task7

import android.media.MediaCodec
import android.media.MediaCodec.Callback
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_7.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class Task7Activity :AppCompatActivity() {
   lateinit  var codec:MediaCodec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_7)
        btn1.setOnClickListener {
            GlobalScope.launch {
                extractAAC()
            }
        }

        btn2.setOnClickListener {
            GlobalScope.launch {
                decodeAAC()
            }
        }
    }

    //从网络地址提取出AAC文件
    suspend fun extractAAC(){
        withContext(Dispatchers.IO){
            val extractor=  MediaExtractor()
            extractor.setDataSource("https://knd-vita.obs.cn-east-3.myhuaweicloud.com/pro/video/1599026012007.mp4?AccessKeyId=XXAQGOL8KYHZVJJJ6E92&Expires=1760382000&Signature=7hY90AVbAOl3KPuTrf3wQeNx7dc%3D")
            for(index in 0 until extractor.trackCount){
                extractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)?.run {
                    if(this.startsWith("audio/")){
                        extractor.selectTrack(index)
                        var file=File(cacheDir,"first.aac")
                        if(file.exists()) file.delete()
                        var fos=FileOutputStream(file)
                        var buffer=ByteBuffer.allocate(1024)
                        do{
                           var len= extractor.readSampleData(buffer,0)
                            if(len<=0) break
                            fos.write(buffer.array(),0,len)
                            fos.flush()
                            extractor.advance()
                        }while (true)

                        fos.flush()
                        fos.close()
                        codec= MediaCodec.createDecoderByType(this)
                        codec.configure(extractor.getTrackFormat(index),null,null,0)
                        extractor.release()

                        Log.e("com.luyy","aac 提取完成")

                    }
                }
            }
        }
    }

    /**
     * 硬解码AAC
     */
    suspend fun decodeAAC(){
        var fis=FileInputStream(File(cacheDir,"first.aac"))
       var file=File(cacheDir,"first.pcm")
        if(file.exists()) file.delete()
        var fos=FileOutputStream(file)
        var byteArray=ByteArray(1024)
        codec.setCallback(object:Callback(){
            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                var buffer=   codec.getOutputBuffer(index)
                    if(buffer!!.hasArray()) {
                        fos.write(buffer!!.array())
                        fos.flush()
                        codec.releaseOutputBuffer(index, true)
                    }
                }

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {

                var len=fis.read(byteArray)
                if(len>0){
                    var buffer=  codec.getInputBuffer(index)
                    buffer?.clear()
                    buffer?.put(byteArray,0,len)
                    codec.queueInputBuffer(index,0,len,0,0)
                }else{
                    Log.e("com.luyy","解码完成")
                }


            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            }

        })
        codec.start()




    }
}