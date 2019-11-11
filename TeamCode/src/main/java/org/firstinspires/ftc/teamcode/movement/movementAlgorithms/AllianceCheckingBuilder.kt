package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.path.heading.*
import com.acmerobotics.roadrunner.trajectory.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*

class AllianceCheckingBuilder(private val underliningBuilder: TrajectoryBuilder) {
    fun reverse(): AllianceCheckingBuilder {
        underliningBuilder.reverse()
        return this
    }

    fun setReversed(reversed: Boolean): AllianceCheckingBuilder {
        underliningBuilder.setReversed(reversed)
        return this
    }

    fun lineTo(end: Vector2d, interpolator: HeadingInterpolator): AllianceCheckingBuilder {
        verifyCorrectedHeading()
        underliningBuilder.lineTo(end, interpolator)
        return this
    }

    fun strafeTo(end: Vector2d): AllianceCheckingBuilder {
        underliningBuilder.strafeTo(end)
        return this
    }

    fun forward(distance: Double): AllianceCheckingBuilder {
        underliningBuilder.forward(distance)
        return this
    }

    fun back(distance: Double): AllianceCheckingBuilder {
        underliningBuilder.back(distance)
        return this
    }

    fun strafeLeft(distance: Double): AllianceCheckingBuilder {
        underliningBuilder.strafeLeft(distance.checkMirror)
        return this

    }

    fun strafeRight(distance: Double): AllianceCheckingBuilder {
        underliningBuilder.strafeRight(distance.checkMirror)
        return this

    }

    fun splineTo(pose: Pose2d, interpolator: HeadingInterpolator = TangentInterpolator()): AllianceCheckingBuilder {
        verifyCorrectedHeading()
        underliningBuilder.splineTo(pose, interpolator)
        return this
    }

    fun addMarker(time: Double, callback: () -> Unit): AllianceCheckingBuilder {
        underliningBuilder.addMarker(time, callback)
        return this
    }

    fun addMarker(point: Vector2d, callback: () -> Unit): AllianceCheckingBuilder {
        underliningBuilder.addMarker(point.checkAlliance, callback)
        return this
    }

    fun addMarker(callback: () -> Unit): AllianceCheckingBuilder {
        underliningBuilder.addMarker(callback)
        return this
    }

    fun build(): Trajectory {
        return underliningBuilder.build()
    }

    fun verifyCorrectedHeading() {
        if (!correctedHeadingInterpolator)
            throw IllegalArgumentException("heading interpolator didn't check alliance")
        correctedHeadingInterpolator = false
    }
}

var correctedHeadingInterpolator = false

fun allianceSplineInterpolator(startHeading_deg: Double, endHeading_deg: Double): SplineInterpolator {
    correctedHeadingInterpolator = true
    return SplineInterpolator(startHeading_deg.toRadians.checkMirror, endHeading_deg.toRadians.checkMirror)
}

fun allianceConstantInterpolator(heading_deg: Double): ConstantInterpolator {
    correctedHeadingInterpolator = true
    return ConstantInterpolator(heading_deg.toRadians.checkMirror)
}

fun allianceLinearInterpolator(startHeading_deg: Double, angle_deg: Double): LinearInterpolator {
    correctedHeadingInterpolator = true
    return LinearInterpolator(startHeading_deg.toRadians.checkMirror, angle_deg.toRadians.checkMirror)
}

fun allianceWiggleInterpolator(amplitude: Double, desiredPeriod: Double, baseInterpolator: HeadingInterpolator): WiggleInterpolator {
    if (!correctedHeadingInterpolator)
        throw IllegalArgumentException("didn't use alliance specific heading for wiggle interpolator")
    correctedHeadingInterpolator = true
    return WiggleInterpolator(amplitude.toRadians, desiredPeriod, baseInterpolator)
}

fun allianceTangentInterpolator(): TangentInterpolator {
    correctedHeadingInterpolator = true
    return TangentInterpolator()
}


val Vector2d.checkAlliance get() = Vector2d(x, y.checkMirror)
val Pose2d.checkAlliance get() = Pose2d(vec().checkAlliance, heading.checkMirror)