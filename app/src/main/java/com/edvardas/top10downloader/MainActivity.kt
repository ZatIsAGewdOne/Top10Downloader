package com.edvardas.top10downloader

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10
    private val TAG = "MainActivity"
    private var feedEntries: ListView? = null
    private var cachedFeedUrl = "Placeholder"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        feedEntries = findViewById(R.id.xmlListView)

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString("feedUrl")!!
            feedLimit = savedInstanceState.getInt("feedLimit")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.mnuFree -> feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid -> feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs -> feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"

            R.id.mnu10, R.id.mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} feedLimit is unchanged")
                }
            }
            R.id.mnuRefresh -> cachedFeedUrl = "Placeholder"
            else -> return super.onOptionsItemSelected(item)
        }
        downloadUrl(String.format(feedUrl, feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("feedUrl", feedUrl)
        outState.putInt("feedLimit", feedLimit)
        super.onSaveInstanceState(outState)
    }

    private fun downloadUrl(feedUrl: String) {
        if (!feedUrl.equals(cachedFeedUrl, ignoreCase = true)) {
            DownloadData().execute(feedUrl)
            cachedFeedUrl = feedUrl
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadData : AsyncTask<String, Unit, String>() {
        private val TAG = "DownloadData"

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val parser = FeedEntryParser()
            parser.parse(result)
            val feedAdapter = FeedAdapter(this@MainActivity, R.layout.list_record, parser.feedEntries)
            feedEntries?.adapter = feedAdapter
        }

        override fun doInBackground(vararg p0: String?): String {
            Log.d(TAG, "doInBackground: starts with ${p0[0]}")
            val rssFeed = downloadXML(p0[0])
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error while downloading data")
            }
            return rssFeed!!
        }

        private fun downloadXML(urlPath: String?): String? {
            val xmlResult = StringBuilder()

            try {
                val url = URL(urlPath)
                val connection = url.openConnection() as HttpURLConnection
                val response = connection.responseCode
                Log.d(TAG, "downloadXML: The response code was $response")
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var charsRead: Int
                val inputBuffer = CharArray(500)
                while (true) {
                    charsRead = reader.read(inputBuffer)
                    if (charsRead < 0) break
                    if (charsRead > 0) xmlResult.append(java.lang.String.copyValueOf(inputBuffer, 0, charsRead))
                }
                reader.close()
                return xmlResult.toString()
            } catch (e: MalformedURLException) {
                Log.e(TAG, "downloadXML: Invalid URL ${e.message}")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e(TAG, "downloadXML: Error while reading data ${e.message}")
                e.printStackTrace()
            } catch (e: SecurityException) {
                Log.e(TAG, "downloadXML: Illegal access due to ${e.message}")
                e.printStackTrace()
            }
            return null
        }
    }
}