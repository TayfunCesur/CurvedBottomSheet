package com.tayfuncesur.curvedbottomsheet

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout


class CurvedLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val mPath: Path
    private val mPaint: Paint
    private val circlePaint: Paint

    var radius: Int = 0
    var fillColor: Int = 0

    var type = CurvedBottomSheet.Type.CURVE
    var shape = CurvedBottomSheet.Shape.Concave
    var location = CurvedBottomSheet.Location.BOTTOM
    var showControlPoints = false

    private val mCurveStartPoint = Point()
    private val mCurveEndPoint = Point()
    private val mControlPoint1 = Point()
    private val mControlPoint2 = Point()


    private var mNavigationBarWidth: Int = 0
    private var mNavigationBarHeight: Int = 0


    init {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CurvedLayout, defStyleAttr, 0)
        fillColor = ta.getColor(R.styleable.CurvedLayout_fillColor, Color.parseColor("#FFFFFF"))
        showControlPoints = ta.getBoolean(R.styleable.CurvedLayout_showControlPoints, false)
        ta.recycle()

        radius = 180
        mPath = Path()
        mPaint = Paint()
        circlePaint = Paint()
        with(mPaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = fillColor
        }
        with(circlePaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.parseColor("#4caf50")
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mNavigationBarWidth = width
        mNavigationBarHeight = height

        if (location == CurvedBottomSheet.Location.TOP)
            mPath.fillType = Path.FillType.INVERSE_EVEN_ODD

        if (type == CurvedBottomSheet.Type.CURVE) {
            if (shape == CurvedBottomSheet.Shape.Concave) {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, radius)
                    mCurveEndPoint.set(mNavigationBarWidth, radius)

                    mControlPoint1.set(
                        mCurveStartPoint.x + radius * 4 / 3,
                        mCurveStartPoint.y - radius
                    )
                    mControlPoint2.set(mCurveEndPoint.x - radius * 4 / 3, mCurveEndPoint.y - radius)
                } else {
                    mCurveStartPoint.set(0, height - radius)
                    mCurveEndPoint.set(mNavigationBarWidth, mNavigationBarHeight - radius)


                    mControlPoint1.set(mCurveStartPoint.x + radius, height)
                    mControlPoint2.set(mCurveEndPoint.x - radius, height)

                }
            } else {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, 0)
                    mCurveEndPoint.set(mNavigationBarWidth, 0)


                    mControlPoint1.set(mCurveStartPoint.x + radius, mCurveStartPoint.y + radius)
                    mControlPoint2.set(mCurveEndPoint.x - radius, mCurveEndPoint.y + radius)
                } else {
                    mCurveStartPoint.set(0, mNavigationBarHeight)
                    mCurveEndPoint.set(mNavigationBarWidth, mNavigationBarHeight)


                    mControlPoint1.set(mCurveStartPoint.x + radius, height - radius)
                    mControlPoint2.set(mCurveEndPoint.x - radius, height - radius)
                }

            }
        } else {
            mCurveStartPoint.set(0, radius)
            mCurveEndPoint.set(mNavigationBarWidth, radius)


            mControlPoint1.set(mCurveStartPoint.x + radius, mCurveStartPoint.y - radius)
            mControlPoint2.set(mCurveEndPoint.x - radius, mCurveEndPoint.y + radius)
        }





        mPath.apply {
            reset()
            moveTo(0f, 0f)
            lineTo(mCurveStartPoint.x.toFloat(), mCurveStartPoint.y.toFloat())

            cubicTo(
                mControlPoint1.x.toFloat(), mControlPoint1.y.toFloat(),
                mControlPoint2.x.toFloat(), mControlPoint2.y.toFloat(),
                mCurveEndPoint.x.toFloat(), mCurveEndPoint.y.toFloat()
            )

            lineTo(mNavigationBarWidth.toFloat(), 0f)
            lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
            lineTo(0f, mNavigationBarHeight.toFloat())
            close()
        }

    }

    fun setCorner(radius: Float) {
        if (type == CurvedBottomSheet.Type.CURVE) {
            if (shape == CurvedBottomSheet.Shape.Concave) {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, radius.toInt())
                    mCurveEndPoint.set(mNavigationBarWidth, radius.toInt())
                } else {
                    mCurveStartPoint.set(0, mNavigationBarHeight - radius.toInt())
                    mCurveEndPoint.set(mNavigationBarWidth, mNavigationBarHeight - radius.toInt())
                }
            } else {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mControlPoint1.set((mCurveStartPoint.x + radius).toInt(), radius.toInt())
                    mControlPoint2.set((mCurveEndPoint.x - radius).toInt(), radius.toInt())
                } else {
                    mControlPoint1.set(
                        (mCurveStartPoint.x + radius).toInt(),
                        mNavigationBarHeight - radius.toInt()
                    )
                    mControlPoint2.set(
                        (mCurveEndPoint.x - radius).toInt(),
                        mNavigationBarHeight - radius.toInt()
                    )
                }
            }
        } else {
            mCurveStartPoint.set(0, radius.toInt())
            mCurveEndPoint.set(mNavigationBarWidth, radius.toInt())
            mControlPoint1.set(
                (mCurveStartPoint.x + radius).toInt(),
                (mCurveStartPoint.y - radius).toInt()
            )
            mControlPoint2.set(
                (mCurveEndPoint.x - radius).toInt(),
                (mCurveEndPoint.y + radius).toInt()
            )
        }


        mPath.apply {
            reset()
            moveTo(0f, 0f)
            lineTo(mCurveStartPoint.x.toFloat(), mCurveStartPoint.y.toFloat())

            cubicTo(
                mControlPoint1.x.toFloat(), mControlPoint1.y.toFloat(),
                mControlPoint2.x.toFloat(), mControlPoint2.y.toFloat(),
                mCurveEndPoint.x.toFloat(), mCurveEndPoint.y.toFloat()
            )

            lineTo(mNavigationBarWidth.toFloat(), 0f)
            lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
            lineTo(0f, mNavigationBarHeight.toFloat())
            close()
            invalidate()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)

        if (showControlPoints) {
            canvas?.drawCircle(mCurveStartPoint.x.toFloat(), mCurveStartPoint.y.toFloat(), 25F, circlePaint)
            canvas?.drawCircle(mControlPoint1.x.toFloat(), mControlPoint1.y.toFloat(), 25F, circlePaint)
            canvas?.drawCircle(mControlPoint2.x.toFloat(), mControlPoint2.y.toFloat(), 25F, circlePaint)
            canvas?.drawCircle(mCurveEndPoint.x.toFloat(), mCurveEndPoint.y.toFloat(), 25F, circlePaint)
        }
    }

}