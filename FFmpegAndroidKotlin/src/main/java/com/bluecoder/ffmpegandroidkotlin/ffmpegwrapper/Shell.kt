package com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper

import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import com.bluecoder.ffmpegandroidkotlin.utils.convertBufferToReadableFormat
import com.bluecoder.ffmpegandroidkotlin.utils.isProcessCompleted
import com.bluecoder.ffmpegandroidkotlin.utils.streamBufferToReadableFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class Shell private constructor() {

    companion object {
        private var instance: Shell? = null
        private var isShellCommandRunning = false

        fun getInstance(): Shell {
            if (instance == null) {
                instance = Shell()
            }
            return instance!!
        }
    }

    fun runCommand(cmd: Array<String>) = flow {

        println("------------------------------ cmd : ${cmd.joinToString(" ")}")
        val process = Runtime.getRuntime().exec(cmd)

        while (!process.isProcessCompleted()) {
            process.errorStream.streamBufferToReadableFormat().collect{
                emit(it)
            }
        }

//        if (process.exitValue() == 0) {
//            emit("end "+process.inputStream.convertBufferToReadableFormat())
//        } else {
//            throw Exception(process.errorStream.convertBufferToReadableFormat())
//        }

    }.flowOn(Dispatchers.IO)

}