package com.tayfuncesur.curvedbottomsheetdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import com.tayfuncesur.curvedbottomsheet.CurvedBottomSheet
import kotlinx.android.synthetic.main.curved_bottom_concave.*
import kotlinx.android.synthetic.main.curved_bottom_concave.bottom_sheet
import kotlinx.android.synthetic.main.mixed.*

class MixedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mixed)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        //To get an beautiful curve, Optimal value is screenWidth/6

        CurvedBottomSheet(radius = (displayMetrics.widthPixels/6).toFloat(),view = bottom_sheet,type = CurvedBottomSheet.Type.WAVE).init()
        CurvedBottomSheet(radius = (displayMetrics.widthPixels/6).toFloat(),view = top_sheet,location = CurvedBottomSheet.Location.TOP).init()
    }
}