package com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper

import android.content.Context
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_BIN_ALIAS
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_HIDE_BANNER
import com.bluecoder.ffmpegandroidkotlin.utils.getFFmpegBinPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File

class FFmpeg(private val context: Context) {

    private val ffmpeg = File(context.filesDir, FFMPEG_BIN_ALIAS)

    fun executeCommand(args: Array<String>): Flow<Result<String>> {
        return flow {
            val isFFmpegBinaryReady = setUpFFmpeg()
            if (!isFFmpegBinaryReady)
                emit(Result.failure(Exception("Could not set up FFmpeg to run on this device")))

            val cmd = args.toMutableList()

            if (cmd.first() == FFMPEG_BIN_ALIAS)
                cmd.removeFirst()

            cmd.add(0,ffmpeg.absolutePath)
            cmd.add(1,FFMPEG_HIDE_BANNER)
            cmd.add("-loglevel")
            cmd.add("info")
            try {
                Shell.getInstance().runCommand(cmd.toTypedArray()).collect {
                    emit(Result.success(it))
                }
            } catch (ex: Exception) {
                emit(Result.failure(ex))
            }

        }.flowOn(Dispatchers.IO)
    }

    private fun setUpFFmpeg(): Boolean {
        if (!ffmpeg.exists())
            copyFFmpegBinaryToAppStorage()

        if (!ffmpeg.canExecute())
            ffmpeg.setExecutable(true)

        return ffmpeg.exists() && ffmpeg.canExecute()
    }

    private fun copyFFmpegBinaryToAppStorage() {
        val inputStream = context.assets.open(getFFmpegBinPath())

        ffmpeg.outputStream().use {
            inputStream.copyTo(it)
        }

        inputStream.close()
    }
}