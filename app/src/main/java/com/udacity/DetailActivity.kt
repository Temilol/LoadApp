package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udacity.model.DownloadOption
import com.udacity.model.DownloadStatus
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var status: DownloadStatus
    private lateinit var downloadOption: DownloadOption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        getExtra()

        setupView()

        ok_button.setOnClickListener { finish() }
    }

    private fun getExtra() {
        downloadOption = intent.extras?.get(DOWNLOAD_OPTION) as DownloadOption
        status = intent.extras?.get(DOWNLOAD_STATUS) as DownloadStatus
    }

    private fun setupView() {
        val downloadText = findViewById<TextView>(R.id.file_name_text)
        val statusText = findViewById<TextView>(R.id.status_text)

        when (downloadOption) {
            DownloadOption.GLIDE -> {
                downloadText.text = getString(R.string.glide)
            }
            DownloadOption.LOAD_APP -> {
                downloadText.text = getString(R.string.loadApp)
            }
            DownloadOption.RETROFIT -> {
                downloadText.text = getString(R.string.retrofit)
            }
        }

        when (status) {
            DownloadStatus.SUCCESS -> {
                statusText.text = getString(R.string.success_status)
                statusText.setTextColor(getColor(R.color.colorPrimary))
            }

            DownloadStatus.FAILURE -> {
                statusText.text = getString(R.string.failure_status)
                statusText.setTextColor(getColor(R.color.red))
            }
        }
    }

    companion object {
        const val DOWNLOAD_OPTION = "DownloadOption"
        const val DOWNLOAD_STATUS = "DownloadStatus"

        fun createIntent(
            context: Context,
            downloadOption: DownloadOption,
            downloadStatus: DownloadStatus
        ) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(DOWNLOAD_OPTION, downloadOption)
                putExtra(DOWNLOAD_STATUS, downloadStatus)
            }
    }

}
