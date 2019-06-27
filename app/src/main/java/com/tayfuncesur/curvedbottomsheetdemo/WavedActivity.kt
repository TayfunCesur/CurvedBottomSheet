package com.tayfuncesur.curvedbottomsheetdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import com.tayfuncesur.curvedbottomsheet.Callback
import com.tayfuncesur.curvedbottomsheet.CurvedBottomSheet
import kotlinx.android.synthetic.main.curved_bottom_concave.*
import kotlinx.android.synthetic.main.curved_bottom_concave.bottom_sheet
import kotlinx.android.synthetic.main.waved.*

class WavedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waved)
        // The other params dont work. Because wave can't support convex or concave

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        //To get an beautiful curve, Optimal value is screenWidth/6

        CurvedBottomSheet(
            radius = (displayMetrics.widthPixels / 6).toFloat(),
            view = bottom_sheet,
            type = CurvedBottomSheet.Type.WAVE,
            callback = object : Callback {
                override fun onSlide(p0: View, p1: Float) {
                    contentLayout.alpha = p1
                    dolphinLayout.alpha = 1 - p1
                }
            }).init()
    }
}