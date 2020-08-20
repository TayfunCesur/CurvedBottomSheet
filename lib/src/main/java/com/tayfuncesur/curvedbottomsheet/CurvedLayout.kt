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


    private var mWidth: Int = 0
    private var mHeight: Int = 0


    init {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CurvedLayout, defStyleAttr, 0)
        fillColor = ta.getColor(R.styleable.CurvedLayout_fillColor, Color.WHITE)
        showControlPoints = ta.getBoolean(R.styleable.CurvedLayout_showControlPoints, false)
        ta.recycle()

        radius = 180
        mPath = Path()
        mPaint = Paint()
        circlePaint = Paint()
        with(mPaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = fillColor
            isAntiAlias = true
        }
        with(circlePaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.parseColor("#4caf50")
            isAntiAlias = true
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width
        mHeight = height

        if (location == CurvedBottomSheet.Location.TOP)
            mPath.fillType = Path.FillType.INVERSE_EVEN_ODD

        if (type == CurvedBottomSheet.Type.CURVE) {
            if (shape == CurvedBottomSheet.Shape.Concave) {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, radius)
                    mCurveEndPoint.set(mWidth, radius)

                    mControlPoint1.set(
                        mCurveStartPoint.x + radius * 4 / 3,
                        mCurveStartPoint.y - radius
                    )
                    mControlPoint2.set(mCurveEndPoint.x - radius * 4 / 3, mCurveEndPoint.y - radius)
                } else {
                    mCurveStartPoint.set(0, height - radius)
                    mCurveEndPoint.set(mWidth, mHeight - radius)


                    mControlPoint1.set(mCurveStartPoint.x + radius, height)
                    mControlPoint2.set(mCurveEndPoint.x - radius, height)

                }
            } else {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, 0)
                    mCurveEndPoint.set(mWidth, 0)


                    mControlPoint1.set(mCurveStartPoint.x + radius, mCurveStartPoint.y + radius)
                    mControlPoint2.set(mCurveEndPoint.x - radius, mCurveEndPoint.y + radius)
                } else {
                    mCurveStartPoint.set(0, mHeight)
                    mCurveEndPoint.set(mWidth, mHeight)


                    mControlPoint1.set(mCurveStartPoint.x + radius, height - radius)
                    mControlPoint2.set(mCurveEndPoint.x - radius, height - radius)
                }

            }
        } else {
            mCurveStartPoint.set(0, radius)
            mCurveEndPoint.set(mWidth, radius)


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

            lineTo(mWidth.toFloat(), 0f)
            lineTo(mWidth.toFloat(), mHeight.toFloat())
            lineTo(0f, mHeight.toFloat())
            close()
        }

    }

    fun setCorner(radius: Float) {
        if (type == CurvedBottomSheet.Type.CURVE) {
            if (shape == CurvedBottomSheet.Shape.Concave) {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mCurveStartPoint.set(0, radius.toInt())
                    mCurveEndPoint.set(mWidth, radius.toInt())
                } else {
                    mCurveStartPoint.set(0, mHeight - radius.toInt())
                    mCurveEndPoint.set(mWidth, mHeight - radius.toInt())
                }
            } else {
                if (location == CurvedBottomSheet.Location.BOTTOM) {
                    mControlPoint1.set((mCurveStartPoint.x + radius).toInt(), radius.toInt())
                    mControlPoint2.set((mCurveEndPoint.x - radius).toInt(), radius.toInt())
                } else {
                    mControlPoint1.set(
                        (mCurveStartPoint.x + radius).toInt(),
                        mHeight - radius.toInt()
                    )
                    mControlPoint2.set(
                        (mCurveEndPoint.x - radius).toInt(),
                        mHeight - radius.toInt()
                    )
                }
            }
        } else {
            mCurveStartPoint.set(0, radius.toInt())
            mCurveEndPoint.set(mWidth, radius.toInt())
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

            lineTo(mWidth.toFloat(), 0f)
            lineTo(mWidth.toFloat(), mHeight.toFloat())
            lineTo(0f, mHeight.toFloat())
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