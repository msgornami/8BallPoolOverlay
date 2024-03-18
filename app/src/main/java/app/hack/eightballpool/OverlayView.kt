package app.hack.eightballpool

import android.content.res.Resources
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.view.isVisible
import app.hack.eightballpool.databinding.BoardOverlayBinding

class OverlayView(val binding: BoardOverlayBinding, private val resources: Resources) {

    private val TAG = "OverlayView"

    private fun showNormal() {
        with(binding) {
            board.visibility = View.VISIBLE
            trickshot.visibility = View.GONE
            nineBall.visibility = View.GONE
            normal.visibility = View.VISIBLE
            val widthCanvas = resources.getDimension(R.dimen.canvasWidth).toInt().toFloat()
            val heightCanvas = resources.getDimension(R.dimen.canvasHeight).toInt().toFloat()
            normal.setPositionCircle(widthCanvas / 2f, heightCanvas / 2f)
            normal.rotation = 0f
        }
    }

    private fun showTrickshot() {
        with(binding) {
            board.visibility = View.VISIBLE
            normal.visibility = View.GONE
            nineBall.visibility = View.GONE
            trickshot.visibility = View.VISIBLE
            val widthCanvas = resources.getDimension(R.dimen.canvasWidth).toInt().toFloat()
            val heightCanvas = resources.getDimension(R.dimen.canvasHeight).toInt().toFloat()
            trickshot.setPositionCircleOne(widthCanvas / 2f - 200, heightCanvas / 2f)
            trickshot.setPositionCircleTwo(widthCanvas / 2f + 200, heightCanvas / 2f)
            trickshot.setPositionControls(widthCanvas - 200, 200f)
            trickshot.rotation = 0f
        }
    }

    private fun showNineBall() {
        with(binding) {
            board.visibility = View.VISIBLE
            normal.visibility = View.GONE
            trickshot.visibility = View.GONE
            nineBall.visibility = View.VISIBLE
            val widthCanvas = resources.getDimension(R.dimen.canvasWidth).toInt().toFloat()
            val heightCanvas = resources.getDimension(R.dimen.canvasHeight).toInt().toFloat()

            // Start line
            val left = widthCanvas - 327
            val top = heightCanvas - 300

            // End line
            val right = widthCanvas - 290
            val bottom = heightCanvas - 282.5f
            nineBall.setPositionCircleOne(widthCanvas / 2f + 345.5f, heightCanvas / 2f + 39f)
            nineBall.setPositionCircleTwo(widthCanvas / 2f - 254, heightCanvas / 2f - 136.5f)
            nineBall.setPositionLine(left, top, right, bottom)
            nineBall.rotation = 0f
        }
    }

    private val showNormal = View.OnClickListener {
        Log.d(TAG, "showNormal")
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        with(binding) {
            btnNormal.setBackgroundResource(R.drawable.button_normal_clicked)
            btnTrickshot.setBackgroundResource(R.drawable.button_trickshot)
            btnNineBall.setBackgroundResource(R.drawable.button_nineball)
        }
        showNormal()
    }

    private val showTrickshot = View.OnClickListener {
        Log.d(TAG, "showTrickshot")
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        with(binding) {
            btnTrickshot.setBackgroundResource(R.drawable.button_trickshot_clicked)
            btnNormal.setBackgroundResource(R.drawable.button_normal)
            btnNineBall.setBackgroundResource(R.drawable.button_nineball)
        }
        showTrickshot()
    }

    private val showNineBall = View.OnClickListener {
        Log.d(TAG, "showNineBall")
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        with(binding) {
            btnNormal.setBackgroundResource(R.drawable.button_normal)
            btnTrickshot.setBackgroundResource(R.drawable.button_trickshot)
            btnNineBall.setBackgroundResource(R.drawable.button_nineball_clicked)
        }
        showNineBall()
    }

    private val hide = View.OnClickListener {
        Log.d(TAG, "hide")
        it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        with(binding) {
            board.visibility = View.GONE
        }
    }

    init {
        binding.btnNormal.setOnClickListener(showNormal)
        binding.btnTrickshot.setOnClickListener(showTrickshot)
        binding.btnNineBall.setOnClickListener(showNineBall)
        binding.btnHide.setOnClickListener(hide)
    }
}
