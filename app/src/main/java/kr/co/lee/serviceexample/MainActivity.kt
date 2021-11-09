package kr.co.lee.serviceexample

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var startForegroundService: Button
    lateinit var stopForegroundService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startForegroundService = findViewById(R.id.fore_service_start)
        stopForegroundService = findViewById(R.id.fore_service_stop)

        startForegroundService.setOnClickListener {
            startForegroundService()
        }

        stopForegroundService.setOnClickListener {
            stopForegroundService()
        }
    }

    // ForegroundService 시작
    private fun startForegroundService() {
        Intent(this, PlayService::class.java).run {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(this)
            else startService(this)
        }
    }

    // ForegroundService 중단
    private fun stopForegroundService() {
        Intent(this, PlayService::class.java).run {
            stopService(this)
        }
    }
}