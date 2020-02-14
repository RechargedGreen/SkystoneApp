package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.checkMirror
import org.firstinspires.ftc.teamcode.movement.PurePursuit.angleBetween_deg
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_deg_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_pose_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode
import org.firstinspires.ftc.teamcode.util.notNaN
import kotlin.math.*

class PurePursuitPath(var followDistance: Double, var moveSpeed: Double = 1.0, var forceMoveSpeedEarly: Boolean = true) {
    val curvePoints = ArrayList<CurvePoint>()

    val firstFollowDistance = followDistance

    fun resetArgs() {
        followDistance = firstFollowDistance
        moveSpeed = 0.0
        forceMoveSpeedEarly = true
    }

    fun toXInternal(x: Double) {
        addInternal(Point(x, curvePoints.last().point.y))
    }

    fun toX(x: Double) {
        toXInternal(x.checkMirror)
    }

    fun addInternal(point: Point) {
        curvePoints.add(CurvePoint(point, followDistance, moveSpeed, forceMoveSpeedEarly))
    }

    fun add(point: Point) {
        addInternal(point.checkMirror)
    }

    fun extrude(distance: Double, angle: Double) {
        extrudeInternal(distance, angle.checkMirror)
    }

    fun extrudeInternal(distance: Double, angle: Double) {
        val rad = angle.toRadians
        val lastPoint = curvePoints.last().point
        addInternal(Point(lastPoint.x + rad.sin * distance, lastPoint.y + rad.cos * distance))
    }

    fun toY(y: Double) {
        addInternal(Point(curvePoints.last().point.x, y))
    }

    fun extend(distance: Double) {
        extrudeInternal(distance, angleBetween_deg(curvePoints[curvePoints.size - 2].point, curvePoints.last().point))
    }

    var finalAngle = Double.NaN
}

val Double.sin get() = Math.sin(this)
val Double.cos get() = Math.cos(this)
val Double.tan get() = Math.tan(this)

object PurePursuit {

    var lastCurvePointIndex = 0

    fun findCurvePoint(allCurvePoints: ArrayList<CurvePoint>, robotLocation: Point): CurvePoint {
        if (allCurvePoints.size < 2) {
            lastCurvePointIndex = max(0, allCurvePoints.size - 1)
            return allCurvePoints[lastCurvePointIndex]
        }

        if (lastIndex > lastCurvePointIndex)
            lastCurvePointIndex = lastIndex

        if (allCurvePoints.size > lastCurvePointIndex + 1 && lastCurvePointIndex > 0) {
            val p0 = allCurvePoints[lastCurvePointIndex - 1].point
            val p1 = allCurvePoints[lastCurvePointIndex].point
            val p2 = allCurvePoints[lastCurvePointIndex + 1].point
            val distance0 = projectToLine(p0, p1, robotLocation)
            val distance1 = projectToLine(p1, p2, robotLocation)

            if (distance1.notNaN())
                if (distance0.isNaN() || distance1 < distance0)
                    lastCurvePointIndex++
        }

        return allCurvePoints[lastCurvePointIndex]
    }

    fun angleBetween_deg(point: Point, otherPoint: Point): Double {
        return atan2(otherPoint.x - point.x, otherPoint.y - point.y).toDegrees
    }

    fun angleWrap_deg(_angle: Double): Double {
        var angle = _angle
        while (angle < -180.0)
            angle += 360.0
        while (angle > 180.0)
            angle -= 360.0
        return angle
    }

    fun distanceBetweenPoints(point: Point, otherPoint: Point): Double {
        return hypot(point.x - otherPoint.x, point.y - otherPoint.y)
    }

    fun goToFollowPoint(targetPoint: Point, robotLocation: Point, followAngle: Double) {
        val angleBetween = angleBetween_deg(robotLocation, targetPoint)
        goToPosition_raw(targetPoint.x, targetPoint.y, angleBetween + followAngle)
    }

    var lastIndex = 0
    var finishingMove = false

    fun reset() {
        lastIndex = 0
        lastCurvePointIndex = 0
        finishingMove = false
        followMe = null
    }

    fun followCurve(path: PurePursuitPath, followAngle: Double = 0.0): Boolean {
        val allPoints = path.curvePoints

        if (allPoints.isEmpty())
            throw IllegalArgumentException("pp path needs a point")

        val finalAngle = path.finalAngle

        val robotLocation = world_pose_raw

        val curvePoint = findCurvePoint(allPoints, robotLocation.point)

        val followMe = getFollowPoint(allPoints, robotLocation, curvePoint.followDistance, followAngle)

        val finalPoint = allPoints.last()

        val distToEndPoint = hypot(finalPoint.point.x - world_x_raw, finalPoint.point.y - world_y_raw)

        if (lastIndex >= allPoints.size - 2 && hypot(finalPoint.point.x - world_x_raw, finalPoint.point.y - world_y_raw) <= 10.0) {
            lastIndex = allPoints.size - 1
            if (finalAngle.notNaN())
                finishingMove = true
        }
        val followMeCurvePoint = if (lastIndex < allPoints.size - 1) allPoints[lastIndex + 1] else allPoints.last()

        val moveSpeed = followMeCurvePoint.moveSpeed

        goToFollowPoint(followMe, robotLocation.point, followAngle)

        if (distanceBetweenPoints(finalPoint.point, robotLocation.point) < followMeCurvePoint.followDistance / 2.0)
            movement_turn = 0.0

        if (finishingMove)
            goToPosition_raw(finalPoint.point.x, finalPoint.point.y, finalAngle)

        return distToEndPoint < 5.0
    }

    var followMe: Point? = null
    fun getFollowPoint(pathPoints: ArrayList<CurvePoint>, robotLocation: Pose, followDistance: Double, followAngle: Double): Point {
        if (followMe == null)
            followMe = pathPoints[min(1, pathPoints.size - 1)].point

        for (i in 0 until min(lastIndex + 2, pathPoints.size - 1)) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val intersections = lineCircleIntersection(robotLocation.point, followDistance, startLine.point.copy(), endLine.point.copy())

            var closestAngle = 1000.0

            mode.telemetry.addData("intersections", intersections.size)
            mode.telemetry.addData("x", world_x_raw)
            mode.telemetry.addData("y", world_y_raw)
            mode.telemetry.addData("deg", world_angle_raw.deg)

            if (intersections.isNotEmpty())
                lastIndex = i

            for (intersection in intersections) {
                val angle = angleWrap_deg((angleBetween_deg(robotLocation.point, intersection) - (world_deg_raw + followAngle))).absoluteValue

                if (angle < closestAngle) {
                    closestAngle = angle
                    followMe = intersection
                }
            }
        }

        val lastPoint = pathPoints.last().point
        val distance = distanceBetweenPoints(robotLocation.point, lastPoint)
        if (distance <= followDistance)
            followMe = lastPoint

        return followMe!!
    }

    fun lineCircleIntersection(center: Point, radius: Double, lineP1: Point, lineP2: Point): ArrayList<Point> {
        if ((lineP1.y - lineP2.y).absoluteValue < 0.003)
            lineP1.y = lineP2.y + 0.003
        if ((lineP1.x - lineP2.x).absoluteValue < 0.003)
            lineP1.x = lineP2.x + 0.003

        val m1 = (lineP2.y - lineP1.y) / (lineP2.x - lineP1.x)

        val quadA = 1.0 + Math.pow(m1, 2.0)

        val x1 = lineP1.x - center.x
        val y1 = lineP1.y - center.y

        val quadB = (2.0 * m1 * y1) - (2.0 * Math.pow(m1, 2.0) * x1)

        val quadC = (Math.pow(m1, 2.0) * Math.pow(x1, 2.0)) - (2.0 * y1 * m1 * x1) + Math.pow(y1, 2.0) - Math.pow(radius, 2.0)

        val points = ArrayList<Point>()

        var xRoot1 = (-quadB + sqrt(Math.pow(quadB, 2.0) - (4.0 * quadA * quadC))) / (2.0 * quadA)

        var yRoot1 = m1 * (xRoot1 - x1) + y1

        xRoot1 += center.x
        yRoot1 += center.y

        val minX = if (lineP1.x < lineP2.x) lineP1.x else lineP2.x
        val maxX = if (lineP1.x > lineP2.x) lineP1.x else lineP2.x

        if (xRoot1.notNaN() && yRoot1.notNaN())
            if (xRoot1 > minX && xRoot1 < maxX)
                points.add(Point(xRoot1, yRoot1))

        var xRoot2 = (-quadB - sqrt(Math.pow(quadB, 2.0) - (4.0 * quadA * quadC))) / (2.0 * quadA)

        var yRoot2 = m1 * (xRoot2 - x1) + y1

        xRoot2 += center.x
        yRoot2 += center.y

        if (xRoot2.notNaN() && yRoot2.notNaN())
            if (xRoot2 > minX && xRoot2 < maxX)
                points.add(Point(xRoot2, yRoot2))

        return points
    }

    fun projectToLine(lineP1: Point, lineP2: Point, robotLocation: Point): Double {
        var distance = Double.NaN

        val minX = if (lineP1.x < lineP2.x) lineP1.x else lineP2.x
        val maxX = if (lineP1.x < lineP2.x) lineP2.x else lineP1.x

        val minY = if (lineP1.y < lineP2.y) lineP1.y else lineP2.y
        val maxY = if (lineP1.y < lineP2.y) lineP2.y else lineP1.y

        if ((lineP1.x - lineP2.x).absoluteValue < 0.003)
            return if (robotLocation.y < maxY && robotLocation.y > minY) (robotLocation.x - lineP1.x).absoluteValue else Double.NaN
        if ((lineP1.y - lineP2.y).absoluteValue < 0.003)
            return if (robotLocation.x < maxX && robotLocation.x > minX) (robotLocation.y - lineP1.y).absoluteValue else Double.NaN

        val m1 = (lineP2.y - lineP1.y) / (lineP2.x - lineP1.x)
        val m2 = -(1.0 / m1)

        val b1 = lineP1.y - m1 * lineP1.x
        val b2 = robotLocation.y - m2 * robotLocation.x

        val x = (m1 * b2 - m1 * b1) / (m1 * m1 + 1)

        if (x > minX && x < maxX) {
            val y = x * m1 + b1
            distance = Math.hypot(x - robotLocation.x, y - robotLocation.y)
        }

        return distance
    }
}

data class IndexedData<type>(val index: Int, val data: type)

data class CurvePoint(var point: Point, var followDistance: Double, val moveSpeed: Double, val forceMoveSpeedEarly: Boolean)