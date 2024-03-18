package app.hack.eightballpool

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import app.hack.eightballpool.databinding.BoardOverlayBinding


class OverlayService : Service() {

    private val TAG = "OverlayService"
    private val CHANNEL_ID = "OverlayServiceChannel"
    private val NOTIFICATION_ID = 888
    private val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    private val windowManager: WindowManager
        get() = getSystemService(WINDOW_SERVICE) as WindowManager

    private lateinit var overlayView: OverlayView

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("InflateParams")
    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        val binding = BoardOverlayBinding.inflate(LayoutInflater.from(this))
        overlayView = OverlayView(binding, resources)
        addView(binding.root)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                Log.d(TAG, ACTION_STOP_SERVICE)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        windowManager.removeView(overlayView.binding.root)
    }

    private fun createNotificationChannel() {
        val name: CharSequence = getString(R.string.app_name)
        val description = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        channel.description = description
        channel.setSound(null, null);

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, OverlayService::class.java)
        stopIntent.setAction(ACTION_STOP_SERVICE)

        val stopPendingIntent =
            PendingIntent.getService(
                this, 0, stopIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_8_ball)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, getString(R.string.stop), stopPendingIntent);

        return builder.build()
    }

    private fun addView(view: View) {
        val boardMarginBottom = resources.getDimension(R.dimen.boardMarginBottom)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        params.gravity = Gravity.BOTTOM or Gravity.CENTER
        params.verticalMargin = boardMarginBottom
        windowManager.addView(view, params)
    }
}
