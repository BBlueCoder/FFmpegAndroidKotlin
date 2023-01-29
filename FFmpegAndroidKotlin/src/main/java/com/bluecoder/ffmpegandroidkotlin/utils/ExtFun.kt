package com.bluecoder.ffmpegandroidkotlin.utils

import android.os.Build.SUPPORTED_ABIS
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses.FileMetadata
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses.Stream
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses.StreamFormat
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_BINARY_FOLDER_NAME
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_BIN_ALIAS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


fun InputStream.convertBufferToReadableFormat() : String {
    val bufferedReader = BufferedReader(InputStreamReader(this))
    val sb = StringBuffer()
    var line = bufferedReader.readLine()
    while(line != null){
        sb.append(line)
        sb.append("\n")
        line = bufferedReader.readLine()
    }
    return sb.toString()
}

fun InputStream.streamBufferToReadableFormat() = flow {
    val bufferedReader = BufferedReader(InputStreamReader(this@streamBufferToReadableFormat))
    var line = bufferedReader.readLine()
    while (line != null) {
        emit(line)
        line = bufferedReader.readLine()
    }
}.flowOn(Dispatchers.IO)

fun Process.isProcessCompleted(): Boolean {
    try {
        this.exitValue()
        return true
    } catch (ex: IllegalThreadStateException) {
        //process not finished yet
    }
    return false
}

fun cpuArch() : String? {
    SUPPORTED_ABIS.forEach {
        when (it) {
            "arm64-v8a" -> return it
            "armeabi-v7a" -> return it
            "x86_64" -> return it
            "x86" -> return it
        }
    }
    return null
}

fun getFFmpegBinPath() : String{
    return "${cpuArch()}${File.separator}$FFMPEG_BINARY_FOLDER_NAME${File.separator}$FFMPEG_BIN_ALIAS"
}

fun extractMetadataFromOutput(output : String):FileMetadata{
    //extract duration
    var startIndex = output.lowercase().indexOf("duration") + "duration:".length
    var endIndex = output.substring(startIndex).indexOfFirst { c -> c == ',' } + startIndex
    val durationText = output.substring(startIndex,endIndex).trim()
    val timeFormat = SimpleDateFormat("HH:mm:ss",Locale.ROOT)
    val date = timeFormat.parse(durationText)
    val durationInMillis = date?.time

    //extract bitrate
    startIndex = output.lowercase().indexOf("bitrate") + "bitrate:".length
    endIndex = output.substring(startIndex).indexOfFirst { c -> c == 's' }+ 1 + startIndex
    val bitrate = output.substring(startIndex,endIndex).trim()

    //count streams
    startIndex = output.lowercase().indexOf("input #0")
    val numberOfStreams = output.substring(startIndex).lowercase().split(" ").count{ it == "stream"}

    val streams = mutableListOf<Stream>()

    for (i in 0 until numberOfStreams){
        //extract stream
        startIndex = output.indexOf("#0:$i") + "#0:$i".length
        startIndex += output.substring(startIndex).indexOf(":") + 1
        endIndex = output.substring(startIndex).lowercase().indexOf("metadata") + startIndex

        val streamMetadata = output.substring(startIndex,endIndex).trim()

        //extract metadata
        var codec : String
        var streamFormat : StreamFormat? = null
        var width : Int? = null
        var height : Int? = null
        var streamBitrate : String?
        var fps : Float? = null

        val metadata = streamMetadata.substring(streamMetadata.indexOf(":")+1).trim().split(",")
        codec = metadata.first()
        streamBitrate = metadata.find { it.contains("/s") }

        when(streamMetadata.substring(0,streamMetadata.indexOf(":")).lowercase()){
            "video" -> {
                streamFormat = StreamFormat.VIDEO
                val res = Regex("\\d{3,4}x(?=\\d{3,4})\\d{3,4}").find(streamMetadata)?.value
                println("********************** $res")
                res?.also {
                    width = Regex("\\d{3,4}(?=x)").find(res)?.value?.toInt()
                    height = Regex("(?<=x)\\d{3,4}").find(res)?.value?.toInt()
                }
                (metadata.find { it.contains("fps") })?.also {
                    fps = it.substring(0,it.indexOf("fps")).trim().toFloat()
                }
            }
            "audio" -> {
                streamFormat = StreamFormat.AUDIO
            }
            "subtitle" -> {
                streamFormat = StreamFormat.SUBTITLE
            }
        }

        val stream = Stream(codec,streamFormat!!,width,height,streamBitrate,fps)
        streams.add(stream)

        println("------------------------------ stream $i : $streamFormat/$codec/$width/$height/$fps/$streamBitrate")

    }

    return FileMetadata(bitrate,durationInMillis!!,streams)
}

