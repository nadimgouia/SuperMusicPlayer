package com.ndroid.supermusicplayer

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import java.io.IOException

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvArtist: TextView
    private lateinit var seekBarTime: SeekBar
    private lateinit var seekBarVolume: SeekBar
    private lateinit var btnPlay: Button

    var musicPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        val song = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("song", Song::class.java)
        }  else {
            intent.getSerializableExtra("song") as Song?
        }
        tvTime = findViewById(R.id.tvTime)
        tvDuration = findViewById(R.id.tvDuration)
        tvTitle = findViewById(R.id.tvTitle)
        tvArtist = findViewById(R.id.tvArtist)
        seekBarTime = findViewById(R.id.seekBarTime)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        btnPlay = findViewById(R.id.btnPlay)

        tvTitle.text = song!!.title
        tvArtist.text = song.artist
        musicPlayer = MediaPlayer()
        try {
            musicPlayer!!.setDataSource(song.path)
            musicPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        musicPlayer!!.isLooping = true
        musicPlayer!!.seekTo(0)
        musicPlayer!!.setVolume(0.5f, 0.5f)
        val duration = millisecondsToString(musicPlayer!!.duration)
        tvDuration.text = duration
        seekBarVolume.progress = 50
        seekBarVolume.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                musicPlayer!!.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        seekBarTime.max = musicPlayer!!.duration
        seekBarTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    musicPlayer!!.seekTo(progress)
                    seekBar.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        btnPlay.setOnClickListener {
            if(musicPlayer!!.isPlaying) {
                musicPlayer!!.pause()
                btnPlay.setBackgroundResource(R.drawable.ic_play)
            } else
            {
                musicPlayer!!.start()
                btnPlay.setBackgroundResource(R.drawable.ic_pause)
            }
        }

        Thread {
            while (musicPlayer != null) {
                if(musicPlayer!!.isPlaying) {
                    try {
                        val current = musicPlayer!!.currentPosition.toDouble()
                        val elapsedTime = millisecondsToString(current.toInt())
                        runOnUiThread {
                            tvTime.text = elapsedTime
                            seekBarTime.progress = current.toInt()
                        }
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        musicPlayer!!.stop()
    }

    private fun millisecondsToString(time: Int): String {
        var elapsedTime: String? = ""
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        elapsedTime = "$minutes:"
        if (seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds
        return elapsedTime
    }

}