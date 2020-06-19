package com.edvardas.top10downloader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FeedAdapter<T : FeedEntry> : ArrayAdapter<Any> {
    private val layoutResource: Int
    private val layoutInflater: LayoutInflater
    val feedEntries: MutableList<T>

    constructor(context: Context, resource: Int, feedEntries: MutableList<T>) : super(context, resource) {
        this.layoutResource = resource
        this.layoutInflater = LayoutInflater.from(context)
        this.feedEntries = feedEntries
    }

    override fun getCount(): Int {
        return feedEntries.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false)

            holder = ViewHolder(convertView)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val currentFeed = feedEntries[position]
        holder.tvName.text = currentFeed.name
        holder.tvArtist.text = currentFeed.artist
        holder.tvSummary.text = currentFeed.summary
        return convertView!!
    }

    private class ViewHolder(v: View) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvSummary: TextView = v.findViewById(R.id.tvSummary)
        val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    }
}