package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.model.DownloadOption
import com.udacity.model.DownloadStatus
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    //    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var downloadUrl: String? = null
    private var downloadOption: DownloadOption? = null
    private lateinit var downloadStatus: DownloadStatus

    private val notificationManager by lazy {
        ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(CHANNEL_ID, CHANNEL_NAME)

        custom_button.setOnClickListener {
            download()
        }

        radio_button_group.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.glide -> {
                    downloadOption = DownloadOption.GLIDE
                    Log.d("MainActivity", "Glide - $downloadOption")
                }
                R.id.loadApp -> {
                    downloadOption = DownloadOption.LOAD_APP
                    Log.d("MainActivity", "LoadApp - $downloadOption")
                }
                R.id.retrofit -> {
                    downloadOption = DownloadOption.RETROFIT
                    Log.d("MainActivity", "Retrofit - $downloadOption")
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                Log.d("MainActivity", "Download Completed")
                custom_button.buttonState = ButtonState.Completed
                val query = DownloadManager.Query().setFilterById(downloadID)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    )
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloadStatus = DownloadStatus.SUCCESS
                        }
                        DownloadManager.STATUS_FAILED -> {
                            downloadStatus = DownloadStatus.FAILURE
                        }
                    }
                    downloadOption?.let {
                        notificationManager?.sendNotification(
                            "Download Completed",
                            applicationContext,
                            downloadStatus,
                            it
                        )
                    }
                }
            }
        }
    }

    private fun download() {
        if (downloadOption != null) {
            val request =
                DownloadManager.Request(Uri.parse(downloadOption?.url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // enqueue puts the download request in the queue.
            downloadID = downloadManager.enqueue(request)
            custom_button.buttonState = ButtonState.Loading
        } else {
            Toast.makeText(applicationContext, "Select an option to download", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(false)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"
//            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "Load App"
    }

}
