package com.google.mediapipe.examples.poselandmarker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var textPaint = Paint()
    private var counterPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private var bicepCurlCount = 0
    private var isArmUp = false

    init {
        initPaints()
    }

    private fun initPaints() {
        linePaint.color = ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textSize = 30f
        textPaint.style = Paint.Style.FILL

        counterPaint.color = Color.RED
        counterPaint.textSize = 50f
        counterPaint.style = Paint.Style.FILL_AND_STROKE
        counterPaint.isFakeBoldText = true
    }

    fun clear() {
        results = null
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->
            val jointConnections = listOf(
                Triple(11, 13, "Left Shoulder-Elbow"),
                Triple(13, 15, "Left Elbow-Wrist"),
                Triple(23, 25, "Left Hip-Knee"),
                Triple(25, 27, "Left Knee-Ankle"),
                Triple(12, 14, "Right Shoulder-Elbow"),
                Triple(14, 16, "Right Elbow-Wrist"),
                Triple(24, 26, "Right Hip-Knee"),
                Triple(26, 28, "Right Knee-Ankle")
            )

            var formFeedback = ""
            poseLandmarkerResult.landmarks().forEach { landmarks ->
                // Draw landmarks and connections
                landmarks.forEach { landmark ->
                    canvas.drawPoint(
                        landmark.x() * imageWidth * scaleFactor,
                        landmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }

                // Draw joint connections and calculate angles
                jointConnections.forEach { (start, end, jointName) ->
                    val startLandmark = landmarks[start]
                    val endLandmark = landmarks[end]

                    canvas.drawLine(
                        startLandmark.x() * imageWidth * scaleFactor,
                        startLandmark.y() * imageHeight * scaleFactor,
                        endLandmark.x() * imageWidth * scaleFactor,
                        endLandmark.y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }

                // Detect bicep curls
                val leftElbowAngle = calculateAngle(
                    landmarks[11].x(), landmarks[11].y(), // Shoulder
                    landmarks[13].x(), landmarks[13].y(), // Elbow
                    landmarks[15].x(), landmarks[15].y()  // Wrist
                )

                if (leftElbowAngle > 160) {
                    if (isArmUp) {
                        bicepCurlCount++
                        isArmUp = false
                    }
                } else if (leftElbowAngle < 50) {
                    isArmUp = true
                }

                // Check for form corrections
                val leftShoulderY = landmarks[11].y()
                val leftElbowY = landmarks[13].y()

                if (leftShoulderY - leftElbowY > 0.2) {
                    formFeedback = "Keep your shoulder stable!"
                } else if (leftElbowAngle > 160 && !isArmUp) {
                    formFeedback = "Straighten your arm fully!"
                }

                // Draw the curl count
                canvas.drawText(
                    "Curls: $bicepCurlCount",
                    20f,
                    60f,
                    counterPaint
                )

                // Draw form feedback
                if (formFeedback.isNotEmpty()) {
                    canvas.drawText(
                        formFeedback,
                        20f,
                        120f,
                        textPaint
                    )
                }
            }
        }
    }

    private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Double {
        val angleRad = atan2(y3 - y2, x3 - x2) - atan2(y1 - y2, x1 - x2)
        val angle = Math.toDegrees(angleRad.toDouble())
        return if (angle < 0) angle + 360 else angle
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> min(width * 1f / imageWidth, height * 1f / imageHeight)
            RunningMode.LIVE_STREAM -> max(width * 1f / imageWidth, height * 1f / imageHeight)
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}
