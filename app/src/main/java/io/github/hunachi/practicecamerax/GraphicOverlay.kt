package io.github.hunachi.practicecamerax

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GraphicOverlay(context: Context, attrs: AttributeSet?): View(context, attrs){


    private val lock = Any()

    private val grapics = mutableSetOf<BoxData>()

    private val rectPoint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2 * resources.displayMetrics.density
    }

    private val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#66000000")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 20 * resources.displayMetrics.density
    }

    private val rect = RectF()
    private val offset = resources.displayMetrics.density * 8
    private val round = resources.displayMetrics.density * 4
    private var scale: Float = 1f
    private var xOffest = 0f
    private var yOffset = 0f

    fun setSize(imageWidth: Int, imageHeight: Int){

        val overlayRatio = width / height.toFloat()
        val imageRatio = imageWidth / imageHeight.toFloat()

        if(overlayRatio < imageRatio) {
            scale = height / imageHeight.toFloat()
            xOffest = (imageWidth * scale - width) * 0.5f
            yOffset = 0f
        }else{
            scale = width / imageWidth.toFloat()
            xOffest = 0f
            yOffset = (imageHeight * scale - height) * 0.5f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock){
            for(grafic in grapics){
                grafic.boundingBox.let {
                    rect.set(
                        it.left * scale,
                        it.top * scale,
                        it.right * scale,
                        it.bottom * scale
                    )
                }

                rect.offset(-xOffest, -yOffset)

                canvas.drawRect(rect, rectPoint)

                if(grafic.text.isNotEmpty()){
                    canvas.drawRoundRect(
                        rect.left,
                        rect.bottom - offset,
                        rect.left + offset + textPaint.measureText(grafic.text) + offset,
                        rect.bottom + textPaint.textSize + offset,
                        round,
                        round,
                        textBackgroundPaint
                    )

                    canvas.drawText(
                        grafic.text,
                        rect.left + offset,
                        rect.bottom + textPaint.textSize,
                        textPaint
                    )
                }
            }
        }
    }


    fun set(list: List<BoxData>){
        synchronized(lock){
            grapics.clear()
            for(boxData in list){
                grapics.add(boxData)
            }
        }

        postInvalidate()
    }
}