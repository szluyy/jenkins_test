package com.example.audiovideosample.task5

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGlSurfaceView(context: Context,attributeSet: AttributeSet):GLSurfaceView(context,attributeSet),GLSurfaceView.Renderer {
    lateinit var triangle:Triangle
    init {
        setEGLContextClientVersion(2)
        setRenderer(this)

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        triangle.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
       triangle= Triangle()
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    }

   companion object{
       fun loadShader(type:Int, shaderCode:String):Int{
            var shader= GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader,shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
   }
}