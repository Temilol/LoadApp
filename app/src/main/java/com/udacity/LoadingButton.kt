package com.udacity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
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
    private var buttonAnimationColor = 0
    private var textColor = 0
    private var circleColor = 0

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        color = context.getColor(R.color.white)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        strokeWidth = strokeWidth
    }

    private val objectAnimator = ObjectAnimator.ofFloat(0F, 360F).apply {
        duration = 1000
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
                objectAnimator.start()
            }
            ButtonState.Clicked -> {
                isEnabled = true
            }
            ButtonState.Completed -> {
                isEnabled = true
                updateButtonLabel(R.string.button_name)
                setBackgroundColor(context.getColor(R.color.colorPrimary))
                objectAnimator.cancel()
            }
        }
        invalidate()
    }


    init {
        setPropertiesWithAttr(context.obtainStyledAttributes(attrs, R.styleable.LoadingButton))
        setBackgroundColor(context.getColor(R.color.colorPrimary))
    }

    private fun setPropertiesWithAttr(typedArray: TypedArray) {
        typedArray.apply {
            try {
                getColor(
                    R.styleable.LoadingButton_circleColor,
                    context.getColor(R.color.colorAccent)
                ).let {
                    circleColor = it
                }
                getColor(
                    R.styleable.LoadingButton_buttonAnimationColor,
                    context.getColor(R.color.darkGreen)
                ).let {
                    buttonAnimationColor = it
                }
                getColor(R.styleable.LoadingButton_textColor, context.getColor(R.color.white)).let {
                    textColor = it
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Background
        if (buttonState != ButtonState.Completed) {
            // Background color animation
            paint.color = buttonAnimationColor
            canvas?.drawRect(
                0F,
                0F,
                value,
                height.toFloat(),
                paint
            )

            //Circle Animation
            paint.color = circleColor
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
        val y = (heightSize / 2 - (paint.descent() + paint.ascent()) / 2)
        val buttonLabel = context.getString(buttonLabelRes)
        paint.color = textColor
        canvas?.drawText(buttonLabel, x, y, paint)
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