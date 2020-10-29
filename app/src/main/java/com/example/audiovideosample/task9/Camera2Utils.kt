package com.example.audiovideosample.task9

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.audiovideosample.LApplication
import java.io.File
import java.io.FileOutputStream

class Camera2Utils private constructor() {
    companion object {
        val instance: Camera2Utils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Camera2Utils() }
    }
    lateinit var  handlerThread:HandlerThread
    lateinit var handler: Handler
    lateinit var mSurface:Surface
    lateinit var mImageReader:ImageReader
    var isRecording=false //是否正在录制视屏
    var callback:RecordCallback?=null //录制一帧视频数据的回调
    val TAG="com.luyy"
    var mCamera:CameraDevice?=null
    private var stateCallback: CameraDevice.StateCallback=object : CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice) {
            mCamera=camera
            var builder=  camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.addTarget(mSurface)
            mImageReader= ImageReader.newInstance(1280,720, ImageFormat.YUV_420_888,10)
            mImageReader.setOnImageAvailableListener({
                var image= it.acquireLatestImage()
                if(image==null) return@setOnImageAvailableListener
                if(isRecording){
                    var buffer= image.planes[0].buffer
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

                    var result=ByteArray(nByteArray.size+arrayUV.size)
                    System.arraycopy(nByteArray,0,result,0,nByteArray.size)
                    System.arraycopy(arrayUV,0,result,nByteArray.size,arrayUV.size)
                    callback?.onFrame(result)
                }else{
                    callback?.onRecordFinish()
                }

                image.close()
            },handler)
            builder.addTarget(mImageReader.surface)
            camera.createCaptureSession(arrayListOf(mSurface,mImageReader.surface),object: CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

                override fun onConfigured(session: CameraCaptureSession) {
                    session.setRepeatingRequest(builder.build(),object : CameraCaptureSession.CaptureCallback(){
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
            Log.e(TAG, "onError:$camera " )
        }

    }


    @SuppressLint("MissingPermission")
    fun startPreivew(surface:Surface){
        this.mSurface=surface
        handlerThread = HandlerThread("cameraBackground")
        handlerThread.start()
        handler=Handler(handlerThread.looper)
        var manager =LApplication.INSTANCE!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        manager.openCamera("0", stateCallback, handler)

    }

    fun startRecord(callback:RecordCallback){
        this.callback=callback
        isRecording=true
    }

    fun stopRecord(){
        isRecording=false
    }

    fun release(){
        mCamera?.close()
        handlerThread.quit()
        handler.removeCallbacksAndMessages(null)
        mSurface.release()
    }

    interface RecordCallback{
        fun onFrame(data:ByteArray) // 经过旋转的yuv420sp数据
        fun onRecordFinish()
    }
}