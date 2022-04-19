/*
Code from: https://github.com/tensorflow/examples/tree/master/lite/examples/pose_estimation/android
==============================================================================
Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package sensors_in_paradise.sonar.page2.camera.pose_estimation

import android.graphics.*
import sensors_in_paradise.sonar.page2.camera.pose_estimation.data.BodyPart
import sensors_in_paradise.sonar.page2.camera.pose_estimation.data.KeyPoint
import sensors_in_paradise.sonar.page2.camera.pose_estimation.data.Person
import kotlin.math.max

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 9f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 6f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    enum class Transformation {
        NORMALIZE,
        ROTATE90,
        PROJECT_ON_CANVAS,
    }

    private fun normalizePoint(p: PointF, inputSize: PointF): PointF {
        return PointF(p.x / inputSize.x, p.y / inputSize.y)
    }

    private fun rotatePoint90(p: PointF): PointF {
        return PointF(1f - p.y, p.x)
    }

    private fun projectPointOnCanvas(
        p: PointF,
        inputSize: PointF,
        outputSize: PointF,
        isRotated: Boolean
    ): PointF {
        // p should be normalized to [0,1]
        val actualInputSize = if (isRotated) PointF(inputSize.y, inputSize.x) else inputSize
        val heightRatio = (actualInputSize.y * outputSize.x) / (outputSize.y * actualInputSize.x)

        val pScaled = PointF(p.x, (p.y * heightRatio) - ((heightRatio - 1f) / 2f))
        return PointF(pScaled.x * outputSize.x, pScaled.y * outputSize.y)
    }

    fun transformKeypoints(
        persons: List<Person>,
        bitmap: Bitmap?,
        canvas: Canvas?,
        transformation: Transformation
    ) {
        val inputSize = bitmap?.let { PointF(bitmap.width.toFloat(), bitmap.height.toFloat()) }
        val outputSize = canvas?.let { PointF(canvas.width.toFloat(), canvas.height.toFloat()) }
        persons.forEach { person ->
            person.keyPoints.forEach { keyPoint ->
                when (transformation) {
                    Transformation.NORMALIZE -> keyPoint.coordinate =
                        normalizePoint(keyPoint.coordinate, inputSize!!)

                    Transformation.ROTATE90 -> keyPoint.coordinate =
                        rotatePoint90(keyPoint.coordinate)

                    Transformation.PROJECT_ON_CANVAS -> keyPoint.coordinate =
                        projectPointOnCanvas(keyPoint.coordinate, inputSize!!, outputSize!!, true)
                }
            }
        }
    }

    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        canvas: Canvas,
        persons: List<Person>,
    ) {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.BLUE
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.WHITE
            style = Paint.Style.STROKE
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        persons.forEach { person ->
            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            person.keyPoints.forEach { point ->
                val coordinate = point.coordinate
                canvas.drawCircle(
                    coordinate.x,
                    coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }
        }
    }
}
