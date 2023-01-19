package com.bluecoder.ffmpegandroidkotlin

import android.content.Context
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.FFmpeg
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.dataclasses.FileMetadata
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_AUDIO_CODEC
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_COPY_CODEC
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_INPUT
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_MAP
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_MAP_AUDIO_FROM_SECOND_INPUT
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_MAP_VIDEO_FROM_FIRST_INPUT
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_SHORTEST
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_STRICT
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_STRICT_NORMAL
import com.bluecoder.ffmpegandroidkotlin.utils.Constants.FFMPEG_VIDEO_CODEC
import com.bluecoder.ffmpegandroidkotlin.utils.extractMetadataFromOutput
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.io.File

class FFmpegWrapper(private val context: Context) {

    //Function to extract input's metadata
    fun extractMetadataFromFile(input : String) = flow<Result<FileMetadata>>{
        val inputFile = File(input)
        if(!inputFile.exists())
            emit(Result.failure(NullPointerException("Input not found")))
        if(!inputFile.canRead())
            emit(Result.failure(IllegalArgumentException("Could not read input")))

        val args = arrayOf(
            FFMPEG_INPUT,
            inputFile.path
        )
        val ffmpeg = FFmpeg(context)
        val sb = StringBuilder()
        ffmpeg.executeCommand(args).onCompletion {
            if(it != null)
                return@onCompletion
            else{
                val fileMetadata = extractMetadataFromOutput(sb.toString())
                emit(Result.success(fileMetadata))
            }
        }.catch {
            emit(Result.failure(it))
        }.collect{output ->
            sb.appendLine(output)
        }

    }

    //Function to combine video and audio
    //the inputs and output paths must be in app storage so ffmpeg can access them and read them
    fun mux(video: String, audio: String, output: String) = flow {
        val videoFile = File(video)
        val audioFile = File(audio)

//        if (!videoFile.exists() || !audioFile.exists())
//            throw NullPointerException("Inputs not found")
//
//        if (!videoFile.canRead() || !audioFile.canRead())
//            throw IllegalArgumentException("Could not read inputs")

        val args = arrayOf(
            FFMPEG_INPUT,
            video,
            FFMPEG_INPUT,
            audio,
            FFMPEG_VIDEO_CODEC,
            FFMPEG_COPY_CODEC,
            FFMPEG_AUDIO_CODEC,
            FFMPEG_COPY_CODEC,
            FFMPEG_STRICT,
            FFMPEG_STRICT_NORMAL,
            FFMPEG_MAP,
            FFMPEG_MAP_VIDEO_FROM_FIRST_INPUT,
            FFMPEG_MAP_AUDIO_FROM_SECOND_INPUT,
            FFMPEG_SHORTEST,
            output
        )

        val ffmpeg = FFmpeg(context)
        ffmpeg.executeCommand(args).collect {
            emit(it)
        }

    }


}