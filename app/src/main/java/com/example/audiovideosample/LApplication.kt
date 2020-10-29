package com.example.audiovideosample

import android.app.Application
import android.content.Context

class LApplication:Application() {
    companion object{
        var INSTANCE:Context?=null
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        INSTANCE=base
    }
}