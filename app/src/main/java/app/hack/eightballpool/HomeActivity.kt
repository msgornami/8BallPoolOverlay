package app.hack.eightballpool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        stopService()
        goToHome()
        finish()
    }

    private fun goToHome() {
        Log.d(TAG, "goToHome")
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        runCatching { startActivity(homeIntent) }
    }

    private fun stopService() {
        Log.d(TAG, "stopService")
        val serviceIntent = Intent(this, OverlayService::class.java)
        serviceIntent.setAction(OverlayService.ACTION_STOP_SERVICE)
        stopService(serviceIntent)
    }
}
