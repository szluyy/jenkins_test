package com.example.audiovideosample.task3

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.AttributeSet
import android.view.TextureView

class CameraTexureView(context: Context, attributes: AttributeSet) :TextureView(context,attributes),TextureView.SurfaceTextureListener{
    lateinit var camera: Camera
    init {
        camera=Camera.open()
        surfaceTextureListener=this
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        camera.release()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        camera.setPreviewTexture(surface)
        camera.setPreviewCallback(Camera.PreviewCallback { data, camera ->

        })
        camera.setDisplayOrientation(90)
        camera.startPreview()
    }
}