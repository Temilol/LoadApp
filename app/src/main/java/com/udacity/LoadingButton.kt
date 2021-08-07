package com.udacity

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.StringRes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private val startAngle = 0F
    private var value = 0F
    private var buttonLabelRes: Int = R.string.button_name
    private val rectF = RectF()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        color = context.getColor(R.color.white)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = strokeWidth
        color = context.getColor(R.color.colorAccent)
    }

    private val animator = ObjectAnimator.ofFloat(0F, 360F).apply {
        duration = 1800
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.RESTART
        interpolator = LinearInterpolator()
        addUpdateListener { objectAnimator ->
            val animatedValue = objectAnimator.animatedValue as Float
            value = animatedValue
            invalidate()
        }
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                isEnabled = false
                updateButtonLabel(R.string.button_loading)
                animator.start()
            }
            ButtonState.Clicked -> {
                isEnabled = true
            }
            ButtonState.Completed -> {
                isEnabled = true
                updateButtonLabel(R.string.button_name)
                setBackgroundColor(context.getColor(R.color.colorPrimary))
                animator.cancel()
            }
        }
        invalidate()
    }


    init {
        setBackgroundColor(context.getColor(R.color.colorPrimary))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Background
        if (buttonState != ButtonState.Completed) {
            // Background color animation
            paint.color = context.getColor(R.color.darkGreen)
            canvas?.drawRect(
                0F,
                0F,
                value,
                height.toFloat(),
                paint
            )

            //Circle Animation
            paint.color = context.getColor(R.color.colorAccent)
            rectF.set(
                widthSize - 200F,
                (heightSize / 2) - 40F,
                widthSize - 120F,
                (heightSize / 2) + 40F
            )
            canvas?.drawArc(rectF, startAngle, value, true, paint)
        } else {
            paint.color = context.getColor(R.color.colorPrimary)
            canvas?.drawRect(
                0F,
                0F,
                width.toFloat(),
                height.toFloat(),
                paint
            )
        }

        // Text
        val x = widthSize.toFloat() / 2
        val y = (heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        val buttonLabel = context.getString(buttonLabelRes)
        canvas?.drawText(buttonLabel, x, y, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun updateButtonLabel(@StringRes stringResId: Int) {
        buttonLabelRes = stringResId
    }
}