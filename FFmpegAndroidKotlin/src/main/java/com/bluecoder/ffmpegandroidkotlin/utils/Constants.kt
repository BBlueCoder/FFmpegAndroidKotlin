package com.bluecoder.ffmpegandroidkotlin.utils

import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.FFmpeg

object Constants {
    const val FFMPEG_BINARY_FOLDER_NAME = "bin"
    const val FFMPEG_BIN_ALIAS = "ffmpeg"

    //FFmpeg options and flags
    const val FFMPEG_INPUT = "-i"

    const val FFMPEG_VIDEO_CODEC = "-c:v"
    const val FFMPEG_AUDIO_CODEC = "-c:a"
    const val FFMPEG_COPY_CODEC = "copy"

    const val FFMPEG_STRICT = "-strict"
    const val FFMPEG_STRICT_EXPERIMENTAL = "experimental"
    const val FFMPEG_STRICT_STRICT = "strict"
    const val FFMPEG_STRICT_NORMAL = "normal"

    const val FFMPEG_SHORTEST = "-shortest"

    const val FFMPEG_MAP = "-map"
    const val FFMPEG_MAP_VIDEO_FROM_FIRST_INPUT = "0:v:0"
    const val FFMPEG_MAP_AUDIO_FROM_SECOND_INPUT = "1:a:0"

    const val FFMPEG_HIDE_BANNER = "-hide_banner"



}