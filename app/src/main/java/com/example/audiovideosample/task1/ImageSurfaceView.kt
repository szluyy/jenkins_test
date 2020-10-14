package com.example.audiovideosample.task1

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.audiovideosample.R

class ImageSurfaceView(context: Context,attributeSet: AttributeSet) : SurfaceView(context,attributeSet),SurfaceHolder.Callback {
   init {
       holder.addCallback(this)
   }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.setFormat(PixelFormat.TRANSLUCENT)
        val canvas=  holder.lockCanvas()
        val paint=Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color=WHITE

        val bitmap=  BitmapFactory.decodeResource(resources,R.mipmap.pic)
        canvas.drawBitmap(bitmap,0f,0f,paint)
        holder.unlockCanvasAndPost(canvas)
    }

}