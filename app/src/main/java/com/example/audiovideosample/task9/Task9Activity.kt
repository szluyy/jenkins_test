package com.example.audiovideosample.task9

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.ImageFormat.YUV_420_888
import android.graphics.YuvImage
import android.hardware.camera2.*
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR
import android.media.*
import android.media.MediaCodec.*
import android.os.*
import android.util.Log
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_9.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

/**
 * 利用Camera2相关api进行视频摄像头的预览
 * 生成yuv数据（去除padding）
 * yuv数据旋转角度
 * android 编码不支持yuv420p 需要使用yuv420sp(即 uv交替)
 */
class Task9Activity :AppCompatActivity(), SurfaceHolder.Callback {
    lateinit var  device:CameraDevice
    lateinit var holder: SurfaceHolder
    lateinit var imageReader:ImageReader
    var toCaputre=false
    var isPrint=false
    var isRecordData=false
    val TAG="com.luyy"
    var trackIndex=-1
    var  isToEnd=false
    var stateCallback:CameraDevice.StateCallback=object :CameraDevice.StateCallback(){
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onOpened(camera: CameraDevice) {
            device=camera
            var builder=  device.createCaptureRequest(TEMPLATE_PREVIEW)
            builder.addTarget(holder.surface)
            imageReader= ImageReader.newInstance(1280,720,ImageFormat.YUV_420_888,10)
            imageReader.setOnImageAvailableListener({
                var image= it.acquireLatestImage()
                if(image==null) return@setOnImageAvailableListener
                if(toCaputre){
                    //拍照
//                    var buffer= image.planes[0].buffer
//                    var byteArray=ByteArray(buffer.limit())
//                    buffer.get(byteArray)
//
//                    var file=File(Environment.getExternalStorageDirectory(),"capture.jpg")
//                    if(file.exists()) file.delete()
//                    var fos=FileOutputStream(file)
//                    fos.write(byteArray)
//                    fos.flush()
//                    fos.close()

                    //(0,0) -->(h,0)
                    //(1,0) -->(h,1)
                    //(2,0) -->(h,2)
                    //....
                    //(w,0) -->(h,w)

                    //(0,1) -->(h-1,0)
                    //(0,2) -->(h-2,0)
                    //(1,1) -->(h-1,1)
                    //(4,5)-->(h-5,4)
                    //(x,y) -->(h-y,x)
                    //录制视频
                    var buffer= image.planes[0].buffer
                    Log.e(TAG, "surfaceCreated: width${image.width},height==${image.height},rowStride===${image.planes[1].rowStride},length==${image.planes[1].buffer.remaining()},pixel====${image.planes[1].pixelStride}" )
                    var bufferU=image.planes[1].buffer
                    var bufferV=image.planes[2].buffer
                    val arrayU=ByteArray(1280*720/4)
                    val arrayV=ByteArray(1280*720/4)
                    val arrayUV=ByteArray(1280*720/2)
                    for(index in 0 until 1280*720/4){
                        arrayU[index]=bufferU.get(index*2)
                        arrayV[index]=bufferV.get(index*2)
                    }



                    var widht=image.width
                    var height=image.height
                    var padding=image.planes[0].rowStride-widht
                   Log.e(TAG,"FORMAT===${image.format}")

                    var byteArray=ByteArray(widht*height)
                    for(i in 0 until height){
                        buffer.get(byteArray,i*widht,widht)
                        buffer.position(i*image.planes[0].rowStride)
                    }
                    var nByteArray=ByteArray(widht*height)
                    var nByteArrayU=ByteArray(widht*height/4)
                    var nByteArrayV=ByteArray(widht*height/4)

//                    //旋转Y分量
                    for(y in 0  until  720){
                        for(x in 0 until 1280){
//                            (x,y) -->(h-y,x)
                           var data= byteArray[y*1280+x]
                            nByteArray[x*720+720-y-1]=data
                        }

                    }
//
                    //旋转U分量
                    for(y in 0  until  720/2){
                        for(x in 0 until 1280/2){
//                            (x,y) -->(h-y,x)
                            var data= arrayU[y*1280/2+x]
                            nByteArrayU[x*720/2+720/2-y-1]=data
                        }

                    }

                    //旋转V分量
                    for(y in 0  until  720/2){
                        for(x in 0 until 1280/2){
//                            (x,y) -->(h-y,x)
                            var data= arrayV[y*1280/2+x]
                            nByteArrayV[x*720/2+720/2-y-1]=data
                        }

                    }


                    for(index in 0 until  1280*720/4){
                        arrayUV[2*index]=nByteArrayU[index]
                        arrayUV[2*index+1]=nByteArrayV[index]
                    }


                    var file=File(Environment.getExternalStorageDirectory(),"capture.yuv")
                    var fos=FileOutputStream(file,true)
//                    fos.write(nByteArray)
//                    fos.write(nByteArrayU)
//                    fos.write(nByteArrayV)
                    fos.write(nByteArray)
//                    fos.write(arrayU)
//                    fos.write(arrayV)
                    fos.write(arrayUV)
                    fos.flush()
                    fos.close()

                }

                image.close()
            },handler)
            builder.addTarget(imageReader.surface)
            device.createCaptureSession(arrayListOf(holder.surface,imageReader.surface),object: CameraCaptureSession.StateCallback() {
               override fun onConfigureFailed(session: CameraCaptureSession) {

               }

               override fun onConfigured(session: CameraCaptureSession) {
                   session.setRepeatingRequest(builder.build(),object :CameraCaptureSession.CaptureCallback(){
                       override fun onCaptureStarted(
                           session: CameraCaptureSession,
                           request: CaptureRequest,
                           timestamp: Long,
                           frameNumber: Long
                       ) {
                           super.onCaptureStarted(session, request, timestamp, frameNumber)

                       }

                       override fun onCaptureProgressed(
                           session: CameraCaptureSession,
                           request: CaptureRequest,
                           partialResult: CaptureResult
                       ) {
                           super.onCaptureProgressed(session, request, partialResult)
                       }
                   },handler)
               }

           },handler)
        }

        override fun onDisconnected(camera: CameraDevice) {
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e("com.luyy","error====$error")
        }

    }


    var handlerThread=HandlerThread("cameraBackground")
    lateinit var handler:Handler
    lateinit var mediaCodec: MediaCodec
    lateinit var mediaMuxer: MediaMuxer
     var startTime=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_9)
        handlerThread.start()
        handler=Handler(handlerThread.looper)
        surface.holder.addCallback(this)
        start.setOnClickListener{
            if(isRecordData){
                //录制视频
                var file=File(Environment.getExternalStorageDirectory(),"capture.yuv")
                if(file.exists()) file.delete()
                toCaputre=true
            }else{
                //yuv数据编码成h264
                mediaCodec= MediaCodec.createEncoderByType("video/avc")
                var format=MediaFormat.createVideoFormat("video/avc",720,1280)
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
                // 调整码率的控流模式
                format.setInteger(MediaFormat.KEY_BIT_RATE, 720*1280*50)
                format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

//
                var file=File(Environment.getExternalStorageDirectory(),"capture.yuv")
                var fis=FileInputStream(file)
                var file2=File(Environment.getExternalStorageDirectory(),"capture.h264")
                if(file2.exists()) file2.delete()

                var file3=File(Environment.getExternalStorageDirectory(),"capture.mp4")
                if(file3.exists()) file3.delete()

                mediaMuxer= MediaMuxer(file3.absolutePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)


                var fos=FileOutputStream(file2,true)
                mediaCodec.configure(format,null,null,CONFIGURE_FLAG_ENCODE)
                Log.e(TAG,"configure================")
                mediaCodec.setCallback(object:MediaCodec.Callback(){
                    override fun onOutputBufferAvailable(
                        codec: MediaCodec,
                        index: Int,
                        info: MediaCodec.BufferInfo
                    ) {

                        var buffer= mediaCodec.getOutputBuffer(index)
                        var byteArray=ByteArray(info.size)
                        buffer?.get(byteArray)
                        fos.write(byteArray)
                        fos.flush()
                            if(info.flags and  BUFFER_FLAG_CODEC_CONFIG!=0){
                                Log.e(TAG,"SPS PPS 帧 size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                                mediaMuxer.writeSampleData(trackIndex,ByteBuffer.wrap(byteArray),info)
                            } else if(info.flags and BUFFER_FLAG_KEY_FRAME!=0){
                                Log.e(TAG,"关键帧 帧  size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                                mediaMuxer.writeSampleData(trackIndex,ByteBuffer.wrap(byteArray),info)
                            }else if(info.flags and BUFFER_FLAG_PARTIAL_FRAME!=0){
                                Log.e(TAG,"部分帧数据")

                            } else if(info.flags and BUFFER_FLAG_END_OF_STREAM!=0){
                                mediaMuxer.writeSampleData(trackIndex, ByteBuffer.wrap(byteArray),info)
                                Log.e(TAG,"数据结尾")
                                mediaMuxer.stop()
                                mediaMuxer.release()
                            }else{
                                Log.e(TAG,"p/B帧 帧   size===${info.size},${info.presentationTimeUs},${info.offset},${info.flags}")
                                mediaMuxer.writeSampleData(trackIndex, ByteBuffer.wrap(byteArray),info)
                            }


                        mediaCodec.releaseOutputBuffer(index,false)
                    }

                    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                        var buffer= mediaCodec.getInputBuffer(index)
                        var byteArray=ByteArray(720*1280*3/2)
                        try {
                            var len= fis.read(byteArray)
                            if(len>0){
                                buffer?.put(byteArray)
                                mediaCodec.queueInputBuffer(index,0,len,startTime,0)
                                startTime+=40_000
                            }else{
                                fis.close()
                                mediaCodec.queueInputBuffer(index,0,0,0,BUFFER_FLAG_END_OF_STREAM)
                            }
                        }  catch (e:Exception){

                        }


                    }

                    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                        Log.e(TAG,"onOutputFormatChanged========")
//                        var data= spilByteArray(byteArray)
//                        var nFormat=   mediaCodec.getOutputFormat()
//                        nFormat.setByteBuffer("csd-0",data?.get(0))
//                        Log.e(TAG,"csd-0===${data?.get(0)?.limit()}")
//                        nFormat.setByteBuffer("csd-1",data?.get(1))
//                        Log.e(TAG,"csd-1===${data?.get(1)?.limit()}")
                        trackIndex=  mediaMuxer.addTrack(format)
                        var frameRate=format.getInteger(MediaFormat.KEY_FRAME_RATE)
                        var bitrate=format.getInteger(MediaFormat.KEY_BIT_RATE)
                        Log.e(TAG,"FRAMERATE============$frameRate")
                        Log.e(TAG,"bitrate============$bitrate")
//                        isToEnd=false
                        mediaMuxer.start()
                    }

                    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                    }

                })

                mediaCodec.start()
            }



        }
        mux.setOnClickListener{
            var mux=MediaMuxer(File(Environment.getExternalStorageDirectory(),"mux.mp4").absolutePath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var format=MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,720,1920)
            mux.addTrack(format)
            mux.start()

        }
    }

    fun spilByteArray(array: ByteArray):ArrayList<ByteBuffer>?{
        var list=ArrayList<ByteBuffer>()
        for(i in array.indices){
            if(i>0 && array[i]==0x00.toByte() && array[i+1]==0x00.toByte()
                && array[i+2]==0x00.toByte()&& array[i+3]==0x01.toByte()){
                var buffer=ByteBuffer.allocate(i)
                var buffer2=ByteBuffer.allocate(array.size-i)
                buffer.put(array,3,i-3)
                buffer2.put(array,i+3,array.size-i-3)
                list.add(buffer)
                list.add(buffer2)
                return  list
            }
        }
        return null
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        this.holder=holder
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
            var manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            manager.openCamera("0", stateCallback, handler)
            var c=  manager.getCameraCharacteristics("0")
            var config=  c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            var sizearray= config?.getOutputSizes(ImageReader::class.java)
            sizearray?.all {
                Log.e(TAG,"height===${it.height},widht====${it.width}")
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        device.close()
    }
}