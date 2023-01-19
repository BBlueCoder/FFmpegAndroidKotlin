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
                val video = File(filesDir,"video_m.mp4")
                val audio = File(filesDir,"audio.m4a")
                fFmpegWrapper.mux(video.path,"tt","output.mp4").onCompletion {
                    if(it != null)
                        println("---------------------incomplet")
                }.catch {

                }.collect{
                    println("------------------------ $it")
                }
            }
//            val video = File(filesDir, "video_m.mp4")
//
//            mediaView.setVideoURI(Uri.fromFile(video))
//            mediaView.requestFocus()
//            mediaView.start()
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