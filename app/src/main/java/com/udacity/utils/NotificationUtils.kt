package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.R
import com.udacity.model.DownloadOption
import com.udacity.model.DownloadStatus

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    downloadStatus: DownloadStatus,
    downloadOption: DownloadOption
) {
    val contentIntent =
        DetailActivity.createIntent(applicationContext, downloadOption, downloadStatus)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    ).apply {
        setSmallIcon(R.drawable.ic_download_cloud)
        setContentTitle(applicationContext.getString(R.string.notification_title))
        setContentText(applicationContext.getString(R.string.notification_description))
        setContentText(messageBody)
        setContentIntent(contentPendingIntent)
        setAutoCancel(true)
        addAction(
            R.drawable.ic_download_cloud,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        priority = NotificationCompat.PRIORITY_HIGH
    }

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}