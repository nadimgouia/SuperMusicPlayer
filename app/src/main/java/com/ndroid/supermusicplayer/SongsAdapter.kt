package com.ndroid.supermusicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SongsAdapter(context: Context, items: ArrayList<Song>) : ArrayAdapter<Song>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val song = getItem(position)
        song?.let {
            tvTitle.text = it.title
            tvArtist.text = it.artist
        }
        return itemView
    }

}
