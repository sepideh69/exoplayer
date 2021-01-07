package com.example.exoplayer.presentation.player

import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.R
import com.google.android.exoplayer2.MediaItem

class SubtitleAdapter( val onSubtitleClick : (uri:Uri)-> Unit) : RecyclerView.Adapter<SubtitleAdapter.SubtitleViewHolder>() {

    private var subtitles = arrayListOf<MediaItem.Subtitle>()


    fun submitList(subtitleList: List<MediaItem.Subtitle>) {
        subtitles.addAll(subtitleList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtitleViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.subtitle_item, parent, false);
        return SubtitleViewHolder(view);
    }


    class SubtitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subtitleV: TextView? = null

        init {
            subtitleV = itemView.findViewById(R.id.subtitle_text_view)
            subtitleV?.apply {
                typeface = ResourcesCompat.getFont(this.context, R.font.iran_sans_font_family)
            }

        }

    }

    override fun onBindViewHolder(holder: SubtitleViewHolder, position: Int) {

        val item = subtitles[position]
        holder.subtitleV?.apply {

            text = item.language
            setOnClickListener {
               onSubtitleClick(item.uri)
            }
        }

    }

    override fun getItemCount(): Int {
        return subtitles.size
    }

}


