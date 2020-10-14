package com.example.audiovideosample.task3

import android.content.Context
import android.hardware.Camera

import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraSurfaceView(context: Context,attributes: AttributeSet):SurfaceView(context,attributes),SurfaceHolder.Callback {
    lateinit var camera:Camera
    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera=Camera.open()
        camera.let {
            it.setPreviewDisplay(holder)
            it.setDisplayOrientation(90)
            it.startPreview()
        }
    }

}