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
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
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
 * 视频录制页面
 * 1、从camera2 中获取 yuv 各通道bytebuffer
 * 2、去除数据中的padding，旋转整合数据 yuv420sp
 * 3、通过h264数据编码，并通过Muxer 打包成mp4文件
 */
class RecordActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view=RecordView(this)
        view.layoutParams=ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT)
        setContentView(view)
    }
}