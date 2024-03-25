package app.hack.eightballpool

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val gamePackage = "com.miniclip.eightballpool"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        getNotificationPermission()
        getDeviceDimensions()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()

        if (Settings.canDrawOverlays(this)) {
            Log.d(TAG, "hasPermission")
            launch()
        } else {
            Log.d(TAG, "getPermission")
            val permissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
            startActivity(permissionIntent)
        }
    }

    private fun getDeviceDimensions() {
        val metrics = resources.displayMetrics;
        val deviceWidth = metrics.widthPixels
        val deviceHeight = metrics.heightPixels
        Log.d(TAG, "Device Width: $deviceWidth")
        Log.d(TAG, "Device Height: $deviceHeight")
    }

    private fun launch() {
        Log.d(TAG, "launch")
        val gameIntent = packageManager.getLaunchIntentForPackage(gamePackage)
        if (intent == null) {
            launchPlayStore()
        } else {
            Log.d(TAG, "launchGame")
            val serviceIntent = Intent(this, OverlayService::class.java)
            serviceIntent.setAction(OverlayService.ACTION_START_SERVICE)
            ContextCompat.startForegroundService(
                this, serviceIntent
            )

            gameIntent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            runCatching { startActivity(gameIntent) }.onFailure {
                Log.e(TAG, "launchGame", it)
            }
        }
        finish()
    }


    private fun launchPlayStore() {
        Log.d(TAG, "launchPlayStore")
        val playStoreUri = "https://play.google.com/store/apps/details?id=$gamePackage"
        val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(playStoreUri)
            setPackage("com.android.vending")
        }

        runCatching { startActivity(playStoreIntent) }.onFailure {
            Log.e(
                TAG, "launchPlayStore", it
            )
        }
    }

    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}
