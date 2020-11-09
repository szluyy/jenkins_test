package com.example.audiovideosample.task5

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.audiovideosample.LApplication
import com.example.audiovideosample.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Rectangle :BaseShape(){
    var vertex_data= floatArrayOf(
        -0.5f,0.5f,0f,
        0.5f,0.5f,0f,
        0.5f,-0.5f,0f,
        -0.5f,-0.5f,0f
    )
    var matrix_data=FloatArray(16)

    var texture_data= floatArrayOf(
        0f,0f,
        1f,0f,
        1f,1f,
        0f,1f
    )

    val vertix_shader="""
        attribute vec4 vPosition;
        attribute vec2 vTexCor;
        uniform mat4 uMat;
        varying vec2 texCor;
        void main(){
            gl_Position = uMat * vPosition;
                texCor=vTexCor;
            }
    """
    val fragment_shader="""
        precision mediump float;
        varying vec2 texCor;
        uniform sampler2D vColor;
        void main(){
            gl_FragColor = texture2D(vColor,texCor);
        }
    """
    //创建opengl 程序
    var program=  GLES20.glCreateProgram()

    lateinit var vertexBuffer:FloatBuffer
    lateinit var texBuffer:FloatBuffer
    lateinit var bitmap: Bitmap
    init {
        GLES20.glAttachShader(program,loadShader(GL_VERTEX_SHADER,vertix_shader))
        GLES20.glAttachShader(program,loadShader(GL_FRAGMENT_SHADER,fragment_shader))
        //链接程序
        GLES20.glLinkProgram(program)

        vertexBuffer=ByteBuffer.allocateDirect(4*vertex_data.size)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertex_data)
        vertexBuffer.position(0)


        texBuffer=ByteBuffer.allocateDirect(4*texture_data.size)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        texBuffer.put(texture_data)
        texBuffer.position(0)
        bitmap=BitmapFactory.decodeResource(LApplication.INSTANCE!!.resources,R.mipmap.timg)



        var textures=IntArray(1)
        GLES20.glGenTextures(1,textures,0)
        glBindTexture(GL_TEXTURE_2D, textures[0])

        //环绕方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0)

        Matrix.setIdentityM(matrix_data,0)
//        Matrix.scaleM(matrix_data,0,2f,2f,2f)
//        Matrix.translateM(matrix_data,0,0.2f,0.2f,0f)
        Matrix.rotateM(matrix_data,0,45f,0f,0f,1f);
    }

    //开始绘制
    override fun draw() {
        //将编译连接好的程序加入到当前环境
        GLES20.glUseProgram(program)

        var matHandler=glGetUniformLocation(program,"uMat")

        glUniformMatrix4fv(matHandler,1,false,matrix_data,0)

        //查找顶点参数的句柄
        var mPostionHandler= GLES20.glGetAttribLocation(program,"vPosition")
        //将数据传入
        GLES20.glVertexAttribPointer(mPostionHandler,3, GL_FLOAT,false,0,vertexBuffer)


        var mTexHandler= GLES20.glGetAttribLocation(program,"vTexCor")
        GLES20.glVertexAttribPointer(mTexHandler,2, GL_FLOAT,false,0,texBuffer)



        //查找颜色参数的句柄
//        var mColorHander= GLES20.glun(program,"vColor")
//        GLES20.glUniform4fv(mColorHander,1, floatArrayOf(1.0f,0.5f,0.4f,1f),0)


//        var mSampler=  GLES20.glGetUniformLocation(program,"vColor")
//        glUniform1i(mSampler,textures[0])

        //开启
        GLES20.glEnableVertexAttribArray(mPostionHandler)
        GLES20.glEnableVertexAttribArray(mTexHandler)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,4)
        GLES20.glDisableVertexAttribArray(mPostionHandler)
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,2,3)


    }


}