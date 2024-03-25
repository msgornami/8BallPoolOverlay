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
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import app.hack.eightballpool.databinding.BoardOverlayBinding


class OverlayService : Service() {

    companion object {
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    }

    private val TAG = "OverlayService"
    private val CHANNEL_ID = "OverlayServiceChannel"
    private val NOTIFICATION_ID = 888

    private val windowManager: WindowManager
        get() = getSystemService(WINDOW_SERVICE) as WindowManager

    private lateinit var overlayView: OverlayView
    private lateinit var button: ImageButton

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("InflateParams")
    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        val binding = BoardOverlayBinding.inflate(LayoutInflater.from(this))
        overlayView = OverlayView(binding, resources)

        button = ImageButton(this).apply {
            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_8_ball, null))
            setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                binding.root.isVisible = !binding.root.isVisible
            }

            setOnLongClickListener {
                removeView(overlayView.binding.root)
                removeView(button)
                true
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        Log.d(TAG, "Action: ${intent?.action}")

        when (intent?.action) {
            ACTION_STOP_SERVICE -> stopService()
            ACTION_START_SERVICE -> {
                addView(overlayView.binding.root)
                addButton(button)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        removeView(button)
        removeView(overlayView.binding.root)
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
                PendingIntent.FLAG_IMMUTABLE
            )

        val startIntent = Intent(this, OverlayService::class.java)
        startIntent.setAction(ACTION_START_SERVICE)

        val startPendingIntent =
            PendingIntent.getService(
                this, 0, startIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val homePendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, HomeActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_8_ball)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, getString(R.string.start), startPendingIntent)
            .addAction(0, getString(R.string.stop), stopPendingIntent)
            .setContentIntent(homePendingIntent)
            .setDeleteIntent(stopPendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }

    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

    private fun addView(view: View) {
        if (view.parent != null) return
        Log.d(TAG, "addView")

        val boardMarginBottom = resources.getDimension(R.dimen.boardMarginBottom)
        val params = layoutParams

        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        params.gravity = Gravity.BOTTOM or Gravity.CENTER
        params.verticalMargin = boardMarginBottom
        windowManager.addView(view, params)
    }

    private fun addButton(view: View) {
        if (view.parent != null) return
        Log.d(TAG, "addButton")

        val boardMarginBottom = resources.getDimension(R.dimen.boardMarginBottom)
        val params = layoutParams

        params.gravity = Gravity.BOTTOM or Gravity.END
        params.verticalMargin = boardMarginBottom
        params.horizontalMargin = boardMarginBottom
        windowManager.addView(view, params)
    }

    private fun removeView(view: View): Boolean {
        if (view.parent != null) {
            Log.d(TAG, "removeView")
            windowManager.removeView(view)
            return true
        }

        return false
    }

    private fun stopService() {
        Log.d(TAG, "stopService")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}
