package com.edvardas.top10downloader

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class FeedEntryParser {
    val feedEntries = ArrayList<FeedEntry>()

    fun parse(xmlData: String?): Boolean {
        var status = true
        var currentEntry: FeedEntry? = null
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(StringReader(xmlData))
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name
                when(eventType) {
                    XmlPullParser.START_TAG -> {
                        if ("entry".equals(tagName, ignoreCase = true)) {
                            inEntry = true
                            currentEntry = FeedEntry()
                        }
                    }
                    XmlPullParser.TEXT -> textValue = xpp.text
                    XmlPullParser.END_TAG -> {
                        if (inEntry) {
                            if ("entry".equals(tagName, ignoreCase = true)) {
                                feedEntries.add(currentEntry!!)
                                inEntry = false
                            } else if ("name".equals(tagName, ignoreCase = true)) {
                                currentEntry?.name = textValue
                            } else if ("artist".equals(tagName, ignoreCase = true)) {
                                currentEntry?.artist = textValue
                            } else if ("releaseDate".equals(tagName, ignoreCase = true)) {
                                currentEntry?.releaseDate = textValue
                            } else if ("summary".equals(tagName, ignoreCase = true)) {
                                currentEntry?.summary = textValue
                            } else if ("image".equals(tagName, ignoreCase = true)) {
                                currentEntry?.imageURL = textValue
                            }
                        }
                    }
                    else -> {}
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            status = false
            e.printStackTrace()
        }
        return status
    }
}