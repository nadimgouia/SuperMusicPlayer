package com.ndroid.supermusicplayer

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ListView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var lvSongs: ListView

    var songsArrayList = ArrayList<Song>()
    var songsAdapter: SongsAdapter? = null

    companion object {
        private const val REQUEST_PERMISSION = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lvSongs = findViewById(R.id.lvSongs)
        songsAdapter = SongsAdapter(this, songsArrayList)
        lvSongs.adapter = songsAdapter

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            return
        } else {
            getSongs()
        }

        lvSongs.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Intent(this@MainActivity, MusicPlayerActivity::class.java).also {
                val song  = songsArrayList[position]
                it.putExtra("song", song)
                startActivity(it)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs()
            }
        }
    }

    private fun getSongs() {
        val songCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)

        if(songCursor != null && songCursor.moveToFirst()) {

            val indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val indexIsMusic = songCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)

            do {
                val title = songCursor.getString(indexTitle)
                var artist = songCursor.getString(indexArtist)
                val path = songCursor.getString(indexData)
                val isMusic = songCursor.getInt(indexIsMusic)

                if(artist == "<unknown>") {
                    artist = "Inconnu"
                }

                if(isMusic == 1) {
                    songsArrayList.add(Song(title, artist, path))
                }

            } while (songCursor.moveToNext())
            songCursor.close()
        }
        songsAdapter?.notifyDataSetChanged()

    }


}