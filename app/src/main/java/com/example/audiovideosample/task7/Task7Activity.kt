package com.example.audiovideosample.task7

import android.media.*
import android.media.MediaCodec.*
import android.media.MediaFormat.KEY_MIME
import android.media.MediaFormat.MIMETYPE_AUDIO_AAC
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovideosample.R
import kotlinx.android.synthetic.main.activity_task_7.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class Task7Activity :AppCompatActivity() {
   lateinit  var codec:MediaCodec
    val sampleRateInHz=44100
    val chanelConfig=AudioFormat.CHANNEL_IN_MONO
    val audioFormat=AudioFormat.ENCODING_PCM_16BIT
    var isRecoding=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_7)
        btn1.setOnClickListener {
            GlobalScope.launch {
                extractAAC()
            }
        }

        btn2.setOnClickListener {
            GlobalScope.launch {
                decodeAAC()
            }
        }

        btn_record.setOnClickListener {
            GlobalScope.launch {
                startRecord()
            }
        }

        btn3.setOnClickListener {
            GlobalScope.launch {
                encodePcm()
            }
        }
    }

    //从网络地址提取出AAC文件
    suspend fun extractAAC(){
        withContext(Dispatchers.IO){
            val extractor=  MediaExtractor()
            var file=File(Environment.getExternalStorageDirectory(),"sample.mp4")
            extractor.setDataSource(file.absolutePath)
            for(index in 0 until extractor.trackCount){
                extractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)?.run {
                    if(this.startsWith("audio/")){
                        extractor.selectTrack(index)
                        var file=File(Environment.getExternalStoragePublicDirectory(
                            DIRECTORY_DOWNLOADS).absoluteFile,"first.aac")
                        if(file.exists()) file.delete()
                        var fos=FileOutputStream(file)
                        var buffer=ByteBuffer.allocate(1024*10)
                        do{

                           var len= extractor.readSampleData(buffer,0)
                            if(len<=0) break
                            var byteArray=ByteArray(len)
                            buffer.get(byteArray)

                            val adtsData = ByteArray(len + 7)
                            addADTStoPacket(adtsData, len+7)
                            System.arraycopy(byteArray, 0, adtsData, 7, len)



                            fos.write(adtsData)
                            buffer.clear()
                            extractor.advance()
                        }while (true)
                        fos.flush()
                        fos.close()

                        extractor.release()

                        Log.e("com.luyy","aac 提取完成")

                    }
                }
            }
        }
    }

    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        /*
        标识使用AAC级别 当前选择的是LC
        一共有1: AAC Main 2:AAC LC (Low Complexity) 3:AAC SSR (Scalable Sample Rate) 4:AAC LTP (Long Term Prediction)
        */
        val profile = 2
        val frequencyIndex = 0x04 //设置采样率
        val channelConfiguration = 2 //设置频道,其实就是声道

        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] =
            ((profile - 1 shl 6) + (frequencyIndex shl 2) + (channelConfiguration shr 2)).toByte()
        packet[3] = ((channelConfiguration and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    /**
     * 硬解码AAC
     */
    suspend fun decodeAAC(){
//        withContext(Dispatchers.IO) {

//            var byteArray = ByteArray(1024 * 10)
//            do {
//                var length = fis.read(byteArray)
//                if (length <= 0) break
//                var index = codec.dequeueInputBuffer(-1)
//                var inputBuffer = codec.getInputBuffer(index)
//                inputBuffer!!.clear()
//                inputBuffer!!.put(byteArray)
//                inputBuffer!!.limit(length)
//
//                val timepts: Long = 1000000L * count / 20
//                codec.queueInputBuffer(index, 0, length, timepts, 0)
//                count++
//                var bufferInfo = MediaCodec.BufferInfo()
//                var outputIndex = codec.dequeueOutputBuffer(bufferInfo, 0)
//                while (outputIndex >= 0) {
//                    var byteBuffer = codec.getOutputBuffer(outputIndex)
//                    if (byteBuffer!!.hasArray()) {
//                        fos.write(byteBuffer!!.array())
//                        fos.flush()
//                    }
//
//                    codec.releaseOutputBuffer(outputIndex, false)
//                    outputIndex = codec.dequeueOutputBuffer(bufferInfo, 0)
//                }
//            } while (true)
//            Log.e("com.luyy", "解码完成")



        var inputFile=  File(
            Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "first.aac"
        )
        var file = File(
            Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
            "first.pcm"
        )
        if (file.exists()) file.delete()

        var extractor=MediaExtractor()
        extractor.setDataSource(inputFile.absolutePath)



        for(index in 0 until extractor.trackCount){
            var mime=extractor.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
            if(mime!!.startsWith("audio/")){
                extractor.selectTrack(index)
                codec= MediaCodec.createDecoderByType(mime)
                codec.configure(extractor.getTrackFormat(index),null,null,0)
            }
        }
        var fos = FileOutputStream(file)
//        var byteArray = ByteArray(1024 * 10)
        codec.setCallback(object: Callback(){
            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                var buffer=   codec.getOutputBuffer(index)
                buffer!!.position(info.offset)
                buffer!!.limit(info.offset+info.size)
                var byteArray=ByteArray(info.size)
                buffer.get(byteArray)
                fos.write(byteArray)
                fos.flush()
                codec.releaseOutputBuffer(index, true)
                }

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                var buffer=  codec.getInputBuffer(index)
                var len= extractor.readSampleData(buffer!!,0)
                if(len>0){
                    codec.queueInputBuffer(index,0,len,extractor.sampleTime,0)
                    extractor.advance()
                }else{
                    codec.queueInputBuffer(index,0,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    Log.e("com.luyy","解码完成")
                }


            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            }

        })
        codec.start()

//        }

    }

    suspend fun startRecord(){
        withContext(Dispatchers.IO){
            val minBufferSize=  AudioRecord.getMinBufferSize(sampleRateInHz,chanelConfig,audioFormat)
            val audioRecord=   AudioRecord(MediaRecorder.AudioSource.MIC,sampleRateInHz,chanelConfig,audioFormat,minBufferSize)
            audioRecord.startRecording()
            isRecoding=true
            var file=File(cacheDir,"hello.pcm")
            var fileOutputStream =FileOutputStream(file)
            var buffer= ByteArray(minBufferSize)
            while (isRecoding){
                audioRecord.read(buffer,0,minBufferSize)
                fileOutputStream.write(buffer)
                fileOutputStream.flush()
            }
            audioRecord.stop()
            fileOutputStream.flush()
            fileOutputStream.close()

        }
    }

    suspend fun encodePcm(){
        var file=File(cacheDir,"hello.pcm")
        var fis=FileInputStream(file)
        var outFile=File( Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),"encoder.aac")
        if(outFile.exists()) outFile.delete()
        var fos=FileOutputStream(outFile)

        val formate=  MediaFormat.createAudioFormat(MIMETYPE_AUDIO_AAC,sampleRateInHz,2)
        formate.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        formate.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 1024)
        formate.setInteger(MediaFormat.KEY_BIT_RATE, 192000)

        var mediaCodec=MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
        mediaCodec.configure(formate,null,null,CONFIGURE_FLAG_ENCODE)
        var byteArray=ByteArray(1024)
        mediaCodec.setCallback(object :Callback(){
            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                var buffer=   codec.getOutputBuffer(index)
                buffer!!.position(info.offset)
                buffer!!.limit(info.offset+info.size)
                var byteArray=ByteArray(info.size)
                buffer.get(byteArray)


                val adtsData = ByteArray(info.size + 7)
                addADTStoPacket(adtsData, info.size+7)
                System.arraycopy(byteArray, 0, adtsData, 7, info.size)

                fos.write(adtsData)
                fos.flush()
                codec.releaseOutputBuffer(index, true)
            }

            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                var length=  fis.read(byteArray)
                if(length>0){
                  var byteBuffer=  mediaCodec.getInputBuffer(index)
                    byteBuffer!!.put(byteArray,0,length)
                    mediaCodec.queueInputBuffer(index,0,length,0,0)
                }else{
                    mediaCodec.queueInputBuffer(index,0,0,0, BUFFER_FLAG_END_OF_STREAM)
                }
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            }

        })

        mediaCodec.start()
    }
}