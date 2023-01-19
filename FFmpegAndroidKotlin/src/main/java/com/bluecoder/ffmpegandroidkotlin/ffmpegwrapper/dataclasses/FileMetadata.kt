package com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses

data class FileMetadata(
    val bitrate : String,
    val durationInMillis : Long,
    val streams : List<Stream>
)
