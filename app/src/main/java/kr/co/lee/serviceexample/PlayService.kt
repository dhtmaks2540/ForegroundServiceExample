package kr.co.lee.serviceexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class PlayService : Service() {
    // Notification Manager 객체 얻기
    private val notificationManager
        get() = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override fun onCreate() {
        super.onCreate()

        registerNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 알림을 제공해서 service를 foreground로 설정(알림 식별 값과 알림 자체를 전달)
        startForeground(NOTIFICATION_DOWNLOAD_ID, createDownloadNotification(0))
        // 스레드를 사용하여 progress update
        thread {
            for(i in 1..100) {
                Thread.sleep(100)
                updateProgress(i)
            }
            // 알림 중단
            stopForeground(true)
            // 서비스 중단
            stopSelf()
            // 완료 알림 보내기
            notificationManager.notify(NOTIFICATION_COMPLETE_ID, createCompleteNotification())
        }

        return START_STICKY
    }

    // Notification Progress 업데이트 하는 메서드
    private fun updateProgress(@IntRange(from = 0L, to = 100L) progress: Int) {
        notificationManager.notify(NOTIFICATION_DOWNLOAD_ID, createDownloadNotification(progress))
    }

    // 다운로드 알림 메서드
    private fun createDownloadNotification(@IntRange(from = 0L, to = 100L) progress: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Download..")
            setContentText("Downloading.....")
            setSmallIcon(android.R.drawable.ic_notification_overlay)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setContentIntent(
                PendingIntent.getActivity(
                    this@PlayService, 0, Intent(this@PlayService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }, 0
                )
            )

            setProgress(100, progress, false)
        }.build()

    // 완료 알림 메서드
    private fun createCompleteNotification() = NotificationCompat.Builder(this, CHANNEL_ID).apply {
        setContentTitle("Download complete")
        setContentText("Complete!!")
        setSmallIcon(android.R.drawable.ic_notification_overlay)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        setContentIntent(
            PendingIntent.getActivity(
                this@PlayService, 0, Intent(this@PlayService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }, 0
            )
        )
    }.build()

    // NotificationChannel 생성(버전 분기)
    private fun registerNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createDeafultNotificationChannel())
        }
    }

    // NotificationChannel 객체 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDeafultNotificationChannel() =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
            description = CHANNEL_DESCRIPTION
            this.setShowBadge(true)
            this.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

    // service와 다른 컴포넌트를 바인딩하지 않기에 null return
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_DOWNLOAD_ID = 1
        private const val NOTIFICATION_COMPLETE_ID = 2
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "default channel"
        private const val CHANNEL_DESCRIPTION = "This is default notification channel"
    }
}