package com.tayfuncesur.curvedbottomsheet

import android.view.View

interface Callback {
    fun onSlide(p0: View, p1: Float)
    fun onStateChanged(p0: View, state: Int)
}