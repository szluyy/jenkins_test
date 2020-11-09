package com.example.audiovideosample.task5

import android.opengl.GLES20

abstract class BaseShape {


    //加载shader 代码
    fun loadShader(type:Int,source:String):Int{
        var shader= GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader,source)
        GLES20.glCompileShader(shader)
        return shader
    }
    //绘制
    abstract fun draw()


    fun loadTexture(){
        var textures=IntArray(1){-1}
        GLES20.glGenTextures(-1,textures,0)
    }
}