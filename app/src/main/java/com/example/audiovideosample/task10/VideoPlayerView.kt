package com.example.audiovideosample.task10

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_task_10.*
import java.io.File
import java.nio.ByteBuffer

 class  VideoPlayerView @JvmOverloads constructor(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) :
    SurfaceView(context, attrs, defStyleAttr),SurfaceHolder.Callback {
    lateinit var mediaExtractor: MediaExtractor
    lateinit var mediaCodec: MediaCodec
    private var path:String?=null
    val TAG="com.luyy"
    init {
        holder.addCallback(this)
    }
    fun startPlay(path:String){
        this.path=path
        mediaExtractor= MediaExtractor()
        mediaExtractor.setDataSource(path)
        var trackFormat:MediaFormat?=null
        for(index in 0 until mediaExtractor.trackCount){
            if(mediaExtractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)?.startsWith("video/")==true){
                mediaExtractor.selectTrack(index)
                trackFormat=mediaExtractor.getTrackFormat(index)
            }
        }

        mediaCodec= MediaCodec.createDecoderByType("video/avc")
        mediaCodec.configure(trackFormat,holder.surface,null,0)
        mediaCodec.setCallback(object :MediaCodec.Callback(){
            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                mediaCodec.releaseOutputBuffer(index,true)
            }

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                var buffer= codec.getInputBuffer(index)
                var len= mediaExtractor.readSampleData(buffer!!,0)
                var frameRate= trackFormat!!.getInteger(MediaFormat.KEY_FRAME_RATE)
                Thread.sleep(1000L/frameRate)
                Log.e(TAG,"readSampleData=======${frameRate}")
                if(len>0){
                    codec.queueInputBuffer(index,0,len,mediaExtractor.sampleTime,mediaExtractor.sampleFlags)
                }else{
                    codec.queueInputBuffer(index,0,0,0,BUFFER_FLAG_END_OF_STREAM)
                }
                mediaExtractor.advance()
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            }

        })

        mediaCodec.start()

    }

     override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

     }

     override fun surfaceDestroyed(holder: SurfaceHolder) {
     }

     override fun surfaceCreated(holder: SurfaceHolder) {
         var file= File(Environment.getExternalStorageDirectory(),"record.mp4")
         startPlay(file.absolutePath)
     }
 }