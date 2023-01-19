package com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses

data class Stream(
    val codec : String,
    val format : StreamFormat,
    val width : Int?,
    val height : Int?,
    val bitrate : String?,
    val fps : Float?
)
