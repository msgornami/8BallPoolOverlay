package app.hack.eightballpool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val gamePackage = "com.miniclip.eightballpool"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

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
            ContextCompat.startForegroundService(
                this, Intent(this, OverlayService::class.java)
            )

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
}
