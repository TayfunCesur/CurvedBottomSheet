package com.tayfuncesur.curvedbottomsheetdemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.tayfuncesur.curvedbottomsheet.CurvedBottomSheet
import com.tayfuncesur.curvedbottomsheet.CurvedLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomAndConcave.setOnClickListener {
            startActivity(Intent(this@MainActivity, CurvedAndBottomAndConcaveActivity::class.java))
        }

        bottomAndConvex.setOnClickListener {
            startActivity(Intent(this@MainActivity, CurvedAndBottomAndConvexActivity::class.java))
        }


        topAndConcave.setOnClickListener {
            startActivity(Intent(this@MainActivity, CurvedAndTopAndConcaveActivity::class.java))
        }


        topAndConvex.setOnClickListener {
            startActivity(Intent(this@MainActivity, CurvedAndTopAndConvexActivity::class.java))
        }

        waveActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity, WavedActivity::class.java))
        }

        mixedActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity, MixedActivity::class.java))
        }

    }
}
