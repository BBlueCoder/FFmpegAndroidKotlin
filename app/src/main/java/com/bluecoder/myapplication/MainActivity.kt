package com.bluecoder.myapplication

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import com.bluecoder.ffmpegandroidkotlin.FFmpegWrapper
import com.bluecoder.ffmpegandroidkotlin.ffmpegwrapper.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn)
        val mediaView = findViewById<VideoView>(R.id.video_view)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(mediaView)
        mediaView.setMediaController(mediaController)

        btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val fFmpegWrapper = FFmpegWrapper(this@MainActivity)
                val video = File(filesDir, "video.mp4")
                val audio = File(filesDir, "audio.m4a")
                val mergedVideo = File(filesDir, "video_m.mp4")

                val audioExtractedFromVideo = File(filesDir, "ext_audio.m4a")
                val audioExtractedFromVideoMP3 = File(filesDir, "ext_audio.mp3")
                fFmpegWrapper.extractAudioFromVideo(mergedVideo.path, audioExtractedFromVideoMP3.path)
                    .onCompletion {
                        if (it != null)
                            println("---------------------incomplet")
                        else {
                            mediaView.setVideoURI(Uri.fromFile(audioExtractedFromVideoMP3))
                            mediaView.requestFocus()
                            mediaView.start()
                        }
                    }.catch {

                    }.collect {
                        println("------------------------ $it")
                    }
            }
//            val video = File(filesDir, "ext_audio.m4a")
////
//            mediaView.setVideoURI(Uri.fromFile(video))
//            mediaView.requestFocus()
//            mediaView.start()

//            copyFileToExternalStorage(R.raw.video,"video.mp4")
//            copyFileToExternalStorage(R.raw.audio,"audio.m4a")
        }


    }

    private fun copyFileToExternalStorage(resource: Int, outputName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = resources.openRawResource(resource)
            File(filesDir, outputName).outputStream().use {
                inputStream.copyTo(it)
            }
        }
    }
}