package com.tayfuncesur.curvedbottomsheetdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import com.tayfuncesur.curvedbottomsheet.CurvedBottomSheet
import kotlinx.android.synthetic.main.curved_bottom_concave.*

class CurvedAndBottomAndConcaveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.curved_bottom_concave)
        // Curved, Bottom and Concave are default params
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        //To get an beautiful curve, Optimal value is screenWidth/6
        CurvedBottomSheet((displayMetrics.widthPixels/6).toFloat(),view = bottom_sheet).init()
    }
}