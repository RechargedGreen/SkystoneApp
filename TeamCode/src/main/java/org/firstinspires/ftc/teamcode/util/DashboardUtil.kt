package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.dashboard.canvas.*
import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.path.*
import org.firstinspires.ftc.teamcode.movement.*
import kotlin.math.*

const val ROBOT_COLOR = "#F44336"
const val PATH_COLOR = "4CAF50"
const val DEFAULT_RESOLUTION = 2.0 // inches
const val ROBOT_RADIUS = 9.0

fun Canvas.drawRobot(pose: Pose2d = DriveMovement.roadRunnerPose2dRaw) {
    setStrokeWidth(1)
    setStroke(ROBOT_COLOR)
    strokeCircle(pose.x, pose.y, ROBOT_RADIUS)

    val v = pose.vec() * ROBOT_RADIUS
    val x1 = pose.x + v.x / 2.0
    val y1 = pose.y + v.y / 2.0
    val x2 = pose.x + v.x
    val y2 = pose.y + v.y
    strokeLine(x1, y1, x2, y2)
}

fun Canvas.drawSampledPath(path: Path, resolution: Double = DEFAULT_RESOLUTION) {
    setStrokeWidth(1)
    setStroke(PATH_COLOR)

    val samples = ceil(path.length() / resolution).toInt()
    val xPoints = DoubleArray(samples)
    val yPoints = DoubleArray(samples)
    val dx = path.length() / (samples - 1)
    for (i in 0 until samples) {
        val displacement = i * dx
        val pose = path[displacement]
        xPoints[i] = pose.x
        yPoints[i] = pose.y
    }
    strokePolyline(xPoints, yPoints)
}