package com.example.audiovideosample.task5

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle {
    val data= floatArrayOf(
            0f,0.5f,0f,
            -0.5f,-0.5f,0f,
            0.5f,-0.5f,0f
    )
    val color= floatArrayOf(
        1f,0f,0f,1f
    )
    lateinit var floatBuffer:FloatBuffer

    val vertexShaderCode="""
        attribute vec4 vPosition;
        void main(){
            gl_Position = vPosition; 
        }
    """

    val fragmentShaderCode1="""
        precision mediump float;
        uniform vec4 vColor;
        void main(){
            gl_FragColor = vColor;
        }
    """
    var program:Int=-1
    var mPostionHandler=-1
    var mColorHandler=-1
    init {
        val byteBuffer=  ByteBuffer.allocateDirect(data.size*4)
        floatBuffer=  byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer()
        floatBuffer.put(data)
        floatBuffer.position(0)

        //创建程式
        program=  GLES20.glCreateProgram()
        GLES20.glAttachShader(program,MyGlSurfaceView.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode))
        GLES20.glAttachShader(program,MyGlSurfaceView.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode))

        GLES20.glLinkProgram(program)
    }


    //绘制
    fun draw(){
        GLES20.glUseProgram(program)
        mPostionHandler= GLES20.glGetAttribLocation(program,"vPosition")
        GLES20.glVertexAttribPointer(mPostionHandler,3,GLES20.GL_FLOAT,false,12,floatBuffer)
        GLES20.glEnableVertexAttribArray(mPostionHandler)

        mColorHandler=  GLES20.glGetUniformLocation(program,"vColor")

        GLES20.glUniform4fv(mColorHandler,1,color,0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3)
        GLES20.glDisableVertexAttribArray(mPostionHandler)
    }
}