package com.tayfuncesur.curvedbottomsheet

import android.graphics.Color
import android.support.design.widget.BottomSheetBehavior
import android.view.View


class CurvedBottomSheet(
    private val radius: Float = 180F,
    private var view: View,
    private val type: Type = Type.CURVE,
    private val location: Location = Location.BOTTOM,
    private val shape: Shape = Shape.Concave,
    private val callback: Callback? = null
) {

    enum class Type {
        CURVE, WAVE
    }

    enum class Location {
        BOTTOM, TOP
    }

    enum class Shape {
        Concave, Convex
    }

    fun init() {
        (view as CurvedLayout).radius = radius.toInt()
        (view as CurvedLayout).type = type
        (view as CurvedLayout).shape = shape
        (view as CurvedLayout).location = location
        view.setBackgroundColor(Color.TRANSPARENT)
        if (location == Location.BOTTOM) {
            val bottomSheetBehavior = BottomSheetBehavior.from(view)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    val ra = radius - (radius * p1)
                    (p0 as CurvedLayout).setCorner(ra)
                    callback?.onSlide(p0,p1)
                }

                override fun onStateChanged(p0: View, p1: Int) {

                }
            })
        } else {
            val topSheetBehavior = TopSheetBehavior.from(view)
            topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {
                    val ra = radius - (radius * slideOffset)
                    (bottomSheet as CurvedLayout).setCorner(ra)
                    callback?.onSlide(bottomSheet,slideOffset)
                }
            })

        }

    }

}