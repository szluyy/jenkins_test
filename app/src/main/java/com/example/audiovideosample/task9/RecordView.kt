package com.example.audiovideosample.task9

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.RelativeLayout
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue


class  RecordView(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) :
    @JvmOverloads RelativeLayout(context, attrs, defStyleAttr) ,SurfaceHolder.Callback{
     val mSurface=SurfaceView(context)
     val mBtn:Button=Button(context)
    lateinit var  mediaCodec: MediaCodec
    lateinit var mediaMuxer:MediaMuxer
    var blockQueue=LinkedBlockingQueue<ByteArray>()
    var startTime=-1L
    var isRecording=false
    var videoTrackIndex=-1
    val TAG="com.luyy"
    var destFile=File(Environment.getExternalStorageDirectory(),"record.mp4")
    init {
        mSurface.layoutParams=LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        mSurface.holder.addCallback(this)
        addView(mSurface)

        var params=LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.addRule(CENTER_HORIZONTAL)
        addView(mBtn,params)
        mBtn.text="开始录制"
        if(destFile.exists()) destFile.delete()
        mBtn.setOnClickListener {
            if(isRecording){
                Camera2Utils.instance.stopRecord()
                isRecording=false
                mBtn.text="开始录制"
            }else{
                Camera2Utils.instance.startRecord(object :Camera2Utils.RecordCallback{
                    override fun onFrame(data: ByteArray) {
                        blockQueue.offer(data)
                    }

                    override fun onRecordFinish() {
                        //录制结束
                        isRecording=false
                    }
                })
                startEncodeAndMux()
                mBtn.text="停止录制"
                isRecording=true

            }

        }

    }


    //开始编码合成
    fun startEncodeAndMux(){
        mediaCodec= MediaCodec.createEncoderByType("video/avc")
        var format= MediaFormat.createVideoFormat("video/avc",720,1280)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        // 调整码率的控流模式
        format.setInteger(MediaFormat.KEY_BIT_RATE, 720*1280*50)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        mediaCodec.configure(format,null,null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec.setCallback(object: MediaCodec.Callback(){
            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {

                var buffer= mediaCodec.getOutputBuffer(index)
                var byteArray=ByteArray(info.size)
                buffer?.get(byteArray)

                if(info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG !=0){
                    Log.e(TAG,"SPS PPS 帧 size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                    mediaMuxer.writeSampleData(videoTrackIndex, ByteBuffer.wrap(byteArray),info)
                } else if(info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME !=0){
                    Log.e(TAG,"关键帧 帧  size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                    mediaMuxer.writeSampleData(videoTrackIndex, ByteBuffer.wrap(byteArray),info)
                }else if(info.flags and MediaCodec.BUFFER_FLAG_PARTIAL_FRAME !=0){
                    Log.e(TAG,"部分帧数据")

                } else if(info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM !=0){
                    mediaMuxer.writeSampleData(videoTrackIndex, ByteBuffer.wrap(byteArray),info)
                    Log.e(TAG,"数据结尾")
                    mediaMuxer.stop()
                    mediaMuxer.release()
                }else{
                    Log.e(TAG,"p/B帧 帧   size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                    mediaMuxer.writeSampleData(videoTrackIndex, ByteBuffer.wrap(byteArray),info)
                }


                mediaCodec.releaseOutputBuffer(index,false)
            }

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                var buffer= mediaCodec.getInputBuffer(index)
                var byteArray= blockQueue.poll()
                if(byteArray==null){
                    if(!isRecording){
                        mediaCodec.queueInputBuffer(index,0,0,0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                    }else{
                        mediaCodec.queueInputBuffer(index,0,0,0,0)
                    }
                }else{
                    buffer?.put(byteArray)
                    mediaCodec.queueInputBuffer(index,0,byteArray.size,startTime,0)
                    startTime+=40_000
                }

            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                videoTrackIndex=  mediaMuxer.addTrack(format)
                mediaMuxer.start()
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            }

        })
        startTime=0L


        mediaMuxer= MediaMuxer(destFile.absolutePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mediaCodec.start()

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Camera2Utils.instance.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.e(TAG,"surfaceCreated=============")
        Camera2Utils.instance.startPreivew(holder.surface)
    }

}