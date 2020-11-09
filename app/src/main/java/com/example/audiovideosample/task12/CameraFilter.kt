package com.example.audiovideosample.task12

import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.audiovideosample.task5.BaseShape
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class CameraFilter(var texturetId:Int):BaseShape() {
    var vertextShader="""
            attribute vec4 vPosition;
            attribute vec2 vTexCoords;
            varying vec2 yuvTexCoords;
            uniform mat4 mTransform;
            void main(){
                gl_Position=mTransform * vPosition;
                yuvTexCoords=vTexCoords;
            }
    """

    var fragShader="""
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        uniform samplerExternalOES yuvTexSampler;
        varying vec2 yuvTexCoords;
        void main(){
            gl_FragColor=texture2D(yuvTexSampler, yuvTexCoords);;
        }
    """

    var vert_array= floatArrayOf(
        -1.0f,1.0f,
        1.0f,1.0f,
        1.0f,-1.0f,
        -1.0f,-1.0f
    )

    var text_array= floatArrayOf(

        0.0f,0.0f,
        1.0f,0f,
        1.0f,1.0f,
        0.0f,1.0f
    )
    var transform_array=FloatArray(16)
    var floatBuffer= ByteBuffer.allocateDirect(4*2*4).order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    var floatBuffer2= ByteBuffer.allocateDirect(4*2*4).order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    var mProgram=-1
    init {
        floatBuffer.clear()
        floatBuffer.put(vert_array)
        floatBuffer.position(0)
        var vertex= loadShader(GLES20.GL_VERTEX_SHADER,vertextShader)
        var frag= loadShader(GLES20.GL_FRAGMENT_SHADER,fragShader)

        floatBuffer2.clear()
        floatBuffer2.put(text_array)
        floatBuffer2.position(0)

        mProgram= GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram,vertex)
        GLES20.glAttachShader(mProgram,frag)
        GLES20.glLinkProgram(mProgram)

        Matrix.setIdentityM(transform_array,0)
        Matrix.rotateM(transform_array,0,90f,0f,0f,1f)
    }


    override fun draw() {
        GLES20.glUseProgram(mProgram)
        var positionHander=GLES20.glGetAttribLocation(mProgram,"vPosition")
        GLES20.glVertexAttribPointer(positionHander,2,GLES20.GL_FLOAT,false,0,floatBuffer)
        GLES20.glEnableVertexAttribArray(positionHander)

        var ttexureHander=GLES20.glGetAttribLocation(mProgram,"vTexCoords")
        GLES20.glVertexAttribPointer(ttexureHander,2,GLES20.GL_FLOAT,false,0,floatBuffer2)
        GLES20.glEnableVertexAttribArray(ttexureHander)
        var transHandler=  GLES20.glGetUniformLocation(mProgram,"mTransform")
        GLES20.glUniformMatrix4fv(transHandler,1,true,transform_array,0)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        var sampler=  GLES20.glGetUniformLocation(mProgram,"yuvTexSampler")
        GLES20.glUniform1i(sampler,0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,4)

    }
}