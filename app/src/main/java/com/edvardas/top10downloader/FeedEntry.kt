package com.edvardas.top10downloader

open class FeedEntry {
    var name: String? = null
    var artist: String? = null
    var releaseDate: String? = null
    var summary: String? = null
    var imageURL: String? = null

    override fun toString(): String {
        return "FeedEntry{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", summary='" + summary + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}'
    }
}