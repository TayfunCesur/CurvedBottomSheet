package com.tayfuncesur.curvedbottomsheet

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.ParcelableCompat
import androidx.core.os.ParcelableCompatCreatorCallbacks
import androidx.core.view.*
import android.util.AttributeSet
import android.view.*

import java.lang.ref.WeakReference
import kotlin.annotation.Retention


/**
 * An interaction behavior plugin for a child view of [CoordinatorLayout] to make it work as
 * a bottom sheet.
 */
class TopSheetBehavior<V : View> : androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<V> {

    private val mMaximumVelocity: Float

    /**
     * Gets the height of the bottom sheet when it is collapsed.
     *
     * @return The height of the collapsed bottom sheet.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_peekHeight
     */
    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels.
     * @attr ref android.support.design.R.styleable#TopSheetBehavior_Params_behavior_peekHeight
     */
    //        mMaxOffset = mParentHeight - peekHeight;
    var peekHeight: Int = 0
        set(peekHeight) {
            field = Math.max(0, peekHeight)
            if (mViewRef != null && mViewRef!!.get() != null) {
                mMinOffset = Math.max(-mViewRef!!.get()?.height!!, -(mViewRef!!.get()?.height?.minus(this.peekHeight))!!)
            }
        }

    private var mMinOffset: Int = 0

    private var mMaxOffset: Int = 0

    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return `true` if this bottom sheet can hide.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_hideable
     */
    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable `true` to make this bottom sheet hideable.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_hideable
     */
    var isHideable: Boolean = false

    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once.
     *
     * @return Whether the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    /**
     * Sets whether this bottom sheet should skip the collapsed state when it is being hidden
     * after it is expanded once. Setting this to true has no effect unless the sheet is hideable.
     *
     * @param skipCollapsed True if the bottom sheet should skip the collapsed state.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Layout_behavior_skipCollapsed
     */
    var skipCollapsed: Boolean = false

    @State
    private var mState = STATE_COLLAPSED

    private var mViewDragHelper: androidx.customview.widget.ViewDragHelper? = null

    private var mIgnoreEvents: Boolean = false

    private var mLastNestedScrollDy: Int = 0

    private var mNestedScrolled: Boolean = false

    private var mParentHeight: Int = 0

    private var mViewRef: WeakReference<V>? = null

    private var mNestedScrollingChildRef: WeakReference<View>? = null

    private var mCallback: TopSheetCallback? = null

    private var mVelocityTracker: VelocityTracker? = null

    private var mActivePointerId: Int = 0

    private var mInitialY: Int = 0

    private var mTouchingScrollingChild: Boolean = false

    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of [.STATE_EXPANDED], [.STATE_COLLAPSED], [.STATE_DRAGGING],
     * and [.STATE_SETTLING].
     */
    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of [.STATE_COLLAPSED], [.STATE_EXPANDED], or
     * [.STATE_HIDDEN].
     */
    // The view is not laid out yet; modify mState and let onLayoutChild handle it later
    var state: Int
        @State
        get() = mState
        set(@State state) {
            if (state == mState) {
                return
            }
            if (mViewRef == null) {
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED ||
                    isHideable && state == STATE_HIDDEN
                ) {
                    mState = state
                }
                return
            }
            val child = mViewRef!!.get() ?: return
            val top: Int
            if (state == STATE_COLLAPSED) {
                top = mMinOffset
            } else if (state == STATE_EXPANDED) {
                top = mMaxOffset
            } else if (isHideable && state == STATE_HIDDEN) {
                top = -child.height
            } else {
                throw IllegalArgumentException("Illegal state argument: $state")
            }
            setStateInternal(STATE_SETTLING)
            if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
                ViewCompat.postOnAnimation(child, SettleRunnable(child, state))
            }
        }

    internal var oldState = mState

    private val yVelocity: Float
        get() {
            mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity)
            return VelocityTrackerCompat.getYVelocity(mVelocityTracker!!, mActivePointerId)
        }

    private val mDragCallback = object : androidx.customview.widget.ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (mState == STATE_DRAGGING) {
                return false
            }
            if (mTouchingScrollingChild) {
                return false
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                val scroll = mNestedScrollingChildRef!!.get()
                if (scroll != null && ViewCompat.canScrollVertically(scroll, -1)) {
                    // Let the content scroll up
                    return false
                }
            }
            return mViewRef != null && mViewRef!!.get() === child
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            dispatchOnSlide(top)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == androidx.customview.widget.ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING)
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top: Int
            @State val targetState: Int
            if (yvel > 0) { // Moving up
                top = mMaxOffset
                targetState = STATE_EXPANDED
            } else if (isHideable && shouldHide(releasedChild, yvel)) {
                top = -mViewRef!!.get()?.getHeight()!!
                targetState = STATE_HIDDEN
            } else if (yvel == 0f) {
                val currentTop = releasedChild.top
                if (Math.abs(currentTop - mMinOffset) > Math.abs(currentTop - mMaxOffset)) {
                    top = mMaxOffset
                    targetState = STATE_EXPANDED
                } else {
                    top = mMinOffset
                    targetState = STATE_COLLAPSED
                }
            } else {
                top = mMinOffset
                targetState = STATE_COLLAPSED
            }
            if (mViewDragHelper!!.settleCapturedViewAt(releasedChild.left, top)) {
                setStateInternal(STATE_SETTLING)
                ViewCompat.postOnAnimation(
                    releasedChild,
                    SettleRunnable(releasedChild, targetState)
                )
            } else {
                setStateInternal(targetState)
            }
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return constrain(top, if (isHideable) -child.height else mMinOffset, mMaxOffset)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return if (isHideable) {
                child.height
            } else {
                mMaxOffset - mMinOffset
            }
        }
    }

    /**
     * Callback for monitoring events about bottom sheets.
     */
    internal abstract class TopSheetCallback {

        /**
         * Called when the bottom sheet changes its state.
         *
         * @param bottomSheet The bottom sheet view.
         * @param newState    The new state. This will be one of [.STATE_DRAGGING],
         * [.STATE_SETTLING], [.STATE_EXPANDED],
         * [.STATE_COLLAPSED], or [.STATE_HIDDEN].
         */
        abstract fun onStateChanged(bottomSheet: View, @State newState: Int)

        /**
         * Called when the bottom sheet is being dragged.
         *
         * @param bottomSheet The bottom sheet view.
         * @param slideOffset The new offset of this bottom sheet within its range, from 0 to 1
         * when it is moving upward, and from 0 to -1 when it moving downward.
         * @param isOpening   detect showing
         */
        abstract fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?)
    }

    /**
     * @hide
     */
    @IntDef(STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    /**
     * Default constructor for instantiating TopSheetBehaviors.
     */
    constructor() {}

    /**
     * Default constructor for inflating TopSheetBehaviors from layout.
     *
     * @param context The [Context].
     * @param attrs   The [AttributeSet].
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BottomSheetBehavior_Layout
        )
        peekHeight = a.getDimensionPixelSize(
            R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, 0
        )
        isHideable =
            a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false)
        skipCollapsed = a.getBoolean(
            R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed,
            false
        )
        a.recycle()
        val configuration = ViewConfiguration.get(context)
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()
    }

    override fun onSaveInstanceState(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V): Parcelable? {
        return super.onSaveInstanceState(parent, child)?.let { SavedState(it, mState) }
    }

    override fun onRestoreInstanceState(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(parent, child, ss.superState!!)
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED
        } else {
            mState = ss.state
        }
    }

    override fun onLayoutChild(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            ViewCompat.setFitsSystemWindows(child, true)
        }
        val savedTop = child.top
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection)
        // Offset the bottom sheet
        mParentHeight = parent.height
        mMinOffset = Math.max(-child.height, -(child.height - peekHeight))
        mMaxOffset = 0
        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset)
        } else if (isHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, -child.height)
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset)
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.top)
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = androidx.customview.widget.ViewDragHelper.create(parent, mDragCallback)
        }
        mViewRef = WeakReference(child)
        mNestedScrollingChildRef = WeakReference<View>(findScrollingChild(child))
        return true
    }

    override fun onInterceptTouchEvent(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (!child.isShown) {
            return false
        }
        val action = MotionEventCompat.getActionMasked(event)
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset()
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mTouchingScrollingChild = false
                mActivePointerId = MotionEvent.INVALID_POINTER_ID
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false
                    return false
                }
            }
            MotionEvent.ACTION_DOWN -> {
                val initialX = event.x.toInt()
                mInitialY = event.y.toInt()
                val scroll = mNestedScrollingChildRef!!.get()
                if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                    mActivePointerId = event.getPointerId(event.actionIndex)
                    mTouchingScrollingChild = true
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID && !parent.isPointInChildBounds(
                    child,
                    initialX,
                    mInitialY
                )
            }
        }
        if (!mIgnoreEvents && mViewDragHelper!!.shouldInterceptTouchEvent(event)) {
            return true
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        val scroll = mNestedScrollingChildRef!!.get()
        return action == MotionEvent.ACTION_MOVE && scroll != null &&
                !mIgnoreEvents && mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, event.x.toInt(), event.y.toInt()) &&
                Math.abs(mInitialY - event.y) > mViewDragHelper!!.touchSlop
    }

    override fun onTouchEvent(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        if (!child.isShown) {
            return false
        }
        val action = MotionEventCompat.getActionMasked(event)
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (mViewDragHelper != null) {
            //no crash
            mViewDragHelper!!.processTouchEvent(event)
            // Record the velocity
            if (action == MotionEvent.ACTION_DOWN) {
                reset()
            }
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain()
            }
            mVelocityTracker!!.addMovement(event)
            // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
            // to capture the bottom sheet in case it is not captured and the touch slop is passed.
            if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
                if (Math.abs(mInitialY - event.y) > mViewDragHelper!!.touchSlop) {
                    mViewDragHelper!!.captureChildView(child, event.getPointerId(event.actionIndex))
                }
            }
        }
        return !mIgnoreEvents
    }

    override fun onStartNestedScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V,
        directTargetChild: View, target: View, nestedScrollAxes: Int
    ): Boolean {
        mLastNestedScrollDy = 0
        mNestedScrolled = false
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, target: View, dx: Int,
        dy: Int, consumed: IntArray
    ) {
        val scrollingChild = mNestedScrollingChildRef!!.get()
        if (target !== scrollingChild) {
            return
        }
        val currentTop = child.top
        val newTop = currentTop - dy
        if (dy > 0) { // Upward
            if (!ViewCompat.canScrollVertically(target, 1)) {
                if (newTop >= mMinOffset || isHideable) {
                    consumed[1] = dy
                    ViewCompat.offsetTopAndBottom(child, -dy)
                    setStateInternal(STATE_DRAGGING)
                } else {
                    consumed[1] = currentTop - mMinOffset
                    ViewCompat.offsetTopAndBottom(child, -consumed[1])
                    setStateInternal(STATE_COLLAPSED)
                }
            }
        } else if (dy < 0) { // Downward
            // Negative to check scrolling up, positive to check scrolling down
            if (newTop < mMaxOffset) {
                consumed[1] = dy
                ViewCompat.offsetTopAndBottom(child, -dy)
                setStateInternal(STATE_DRAGGING)
            } else {
                consumed[1] = currentTop - mMaxOffset
                ViewCompat.offsetTopAndBottom(child, -consumed[1])
                setStateInternal(STATE_EXPANDED)
            }
        }
        dispatchOnSlide(child.top)
        mLastNestedScrollDy = dy
        mNestedScrolled = true
    }

    override fun onStopNestedScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, target: View) {
        if (child.top == mMaxOffset) {
            setStateInternal(STATE_EXPANDED)
            return
        }
        if (target !== mNestedScrollingChildRef!!.get() || !mNestedScrolled) {
            return
        }
        val top: Int
        val targetState: Int
        if (mLastNestedScrollDy < 0) {
            top = mMaxOffset
            targetState = STATE_EXPANDED
        } else if (isHideable && shouldHide(child, yVelocity)) {
            top = -child.height
            targetState = STATE_HIDDEN
        } else if (mLastNestedScrollDy == 0) {
            val currentTop = child.top
            if (Math.abs(currentTop - mMinOffset) > Math.abs(currentTop - mMaxOffset)) {
                top = mMaxOffset
                targetState = STATE_EXPANDED
            } else {
                top = mMinOffset
                targetState = STATE_COLLAPSED
            }
        } else {
            top = mMinOffset
            targetState = STATE_COLLAPSED
        }
        if (mViewDragHelper!!.smoothSlideViewTo(child, child.left, top)) {
            setStateInternal(STATE_SETTLING)
            ViewCompat.postOnAnimation(child, SettleRunnable(child, targetState))
        } else {
            setStateInternal(targetState)
        }
        mNestedScrolled = false
    }

    override fun onNestedPreFling(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, target: View,
        velocityX: Float, velocityY: Float
    ): Boolean {
        return target === mNestedScrollingChildRef!!.get() && (mState != STATE_EXPANDED || super.onNestedPreFling(
            coordinatorLayout, child, target,
            velocityX, velocityY
        ))
    }

    /**
     * Sets a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    internal fun setTopSheetCallback(callback: TopSheetCallback) {
        mCallback = callback
    }

    private fun setStateInternal(@State state: Int) {
        if (state == TopSheetBehavior.STATE_COLLAPSED || state == TopSheetBehavior.STATE_EXPANDED) {
            oldState = state
        }

        if (mState == state) {
            return
        }
        mState = state
        val bottomSheet = mViewRef!!.get()
        if (bottomSheet != null && mCallback != null) {
            mCallback!!.onStateChanged(bottomSheet, state)
        }
    }

    private fun reset() {
        mActivePointerId = androidx.customview.widget.ViewDragHelper.INVALID_POINTER
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun shouldHide(child: View, yvel: Float): Boolean {
        if (child.top > mMinOffset) {
            // It should not hide, but collapse.
            return false
        }
        val newTop = child.top + yvel * HIDE_FRICTION
        return Math.abs(newTop - mMinOffset) / peekHeight.toFloat() > HIDE_THRESHOLD
    }

    private fun findScrollingChild(view: View): View? {
        if (view is NestedScrollingChild) {
            return view
        }
        if (view is ViewGroup) {
            var i = 0
            val count = view.childCount
            while (i < count) {
                val scrollingChild = findScrollingChild(view.getChildAt(i))
                if (scrollingChild != null) {
                    return scrollingChild
                }
                i++
            }
        }
        return null
    }

    private fun dispatchOnSlide(top: Int) {
        val bottomSheet = mViewRef!!.get()
        if (bottomSheet != null && mCallback != null) {

            val isOpening = oldState == TopSheetBehavior.STATE_COLLAPSED

            if (top < mMinOffset) {
                mCallback!!.onSlide(bottomSheet, (top - mMinOffset).toFloat() / peekHeight, isOpening)
            } else {
                mCallback!!.onSlide(
                    bottomSheet,
                    (top - mMinOffset).toFloat() / (mMaxOffset - mMinOffset), isOpening
                )
            }
        }
    }

    private inner class SettleRunnable internal constructor(
        private val mView: View, @param:State @field:State
        private val mTargetState: Int
    ) : Runnable {

        override fun run() {
            if (mViewDragHelper != null && mViewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this)
            } else {
                setStateInternal(mTargetState)
            }
        }
    }

    protected class SavedState : androidx.customview.view.AbsSavedState {
        @State
        internal val state: Int

        @JvmOverloads
        constructor(source: Parcel, loader: ClassLoader? = null) : super(source, loader) {

            state = source.readInt()
        }

        constructor(superState: Parcelable, @State state: Int) : super(superState) {
            this.state = state
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {

            @SuppressLint("ParcelCreator")
            val CREATOR: Parcelable.Creator<SavedState> = ParcelableCompat.newCreator(
                object : ParcelableCompatCreatorCallbacks<SavedState> {
                    override fun createFromParcel(`in`: Parcel, loader: ClassLoader): SavedState {
                        return SavedState(`in`, loader)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                })
        }
    }

    companion object {

        /**
         * The bottom sheet is dragging.
         */
        const val STATE_DRAGGING = 1

        /**
         * The bottom sheet is settling.
         */
        const val STATE_SETTLING = 2

        /**
         * The bottom sheet is expanded.
         */
        const val STATE_EXPANDED = 3

        /**
         * The bottom sheet is collapsed.
         */
        const val STATE_COLLAPSED = 4

        /**
         * The bottom sheet is hidden.
         */
        const val STATE_HIDDEN = 5

        private val HIDE_THRESHOLD = 0.5f

        private val HIDE_FRICTION = 0.1f

        /**
         * A utility function to get the [TopSheetBehavior] associated with the `view`.
         *
         * @param view The [View] with [TopSheetBehavior].
         * @return The [TopSheetBehavior] associated with the `view`.
         */
        fun <V : View> from(view: V): TopSheetBehavior<V> {
            val params = view.layoutParams as? androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
                ?: throw IllegalArgumentException("The view is not a child of CoordinatorLayout")
            val behavior = params
                .behavior as? TopSheetBehavior<*> ?: throw IllegalArgumentException(
                "The view is not associated with TopSheetBehavior"
            )
            return behavior as TopSheetBehavior<V>
        }

        internal fun constrain(amount: Int, low: Int, high: Int): Int {
            return if (amount < low) low else if (amount > high) high else amount
        }

        internal fun constrain(amount: Float, low: Float, high: Float): Float {
            return if (amount < low) low else if (amount > high) high else amount
        }
    }
}
