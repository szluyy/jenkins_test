package com.example.audiovideosample.task12

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import com.example.audiovideosample.task9.Camera2Utils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class LGlSurfaceView @JvmOverloads constructor(context: Context?, attrs: AttributeSet?=null)
    : GLSurfaceView(context, attrs),GLSurfaceView.Renderer,SurfaceTexture.OnFrameAvailableListener {
    val TAG="com.luyy"
    lateinit var surfaceTexture: SurfaceTexture
   lateinit var textures:IntArray
    lateinit var cameraFilter: CameraFilter
    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode=RENDERMODE_WHEN_DIRTY
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture.updateTexImage()
        cameraFilter.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, Math.min(width,height),  Math.min(width,height))

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        textures=IntArray(1)
        GLES20.glGenTextures(0,textures,0)
        GLES20.glBindTexture(GLES11Ext.GL_SAMPLER_EXTERNAL_OES,textures[0])
        surfaceTexture=SurfaceTexture(textures[0])
        surfaceTexture.setOnFrameAvailableListener(this)
        var surface=Surface(surfaceTexture)
        Camera2Utils.instance.startPreivew(surface)
        cameraFilter= CameraFilter(textures[0])

    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        Log.e(TAG,"onFrameAvailable============")
        requestRender()
    }

}