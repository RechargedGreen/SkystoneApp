package org.firstinspires.ftc.teamcode.movement.roadRunner

import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.path.*
import com.acmerobotics.roadrunner.profile.*
import com.acmerobotics.roadrunner.trajectory.*
import com.acmerobotics.roadrunner.trajectory.constraints.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import kotlin.math.*

private fun zeroPosition(state: MotionState) = MotionState(0.0, state.v, state.a, state.j)

/**
 * Builder for trajectories with *dynamic* constraints.
 */
class SmartTrajectoryBuilder private constructor(
        startPose: Pose2d?,
        startHeading: Double?,
        trajectory: Trajectory?,
        t: Double?,
        private val constraints: TrajectoryConstraints,
        private val start: MotionState,
        private val resolution: Double
) : BaseTrajectoryBuilder(startPose, startHeading, trajectory, t) {
    /**
     * Create a builder from a start pose and motion state. This is the recommended constructor for creating
     * trajectories from rest.
     */
    @JvmOverloads
    constructor(
            startPose: Pose2d,
            startHeading: Double = startPose.heading,
            constraints: TrajectoryConstraints,
            start: MotionState = MotionState(0.0, 0.0, 0.0),
            resolution: Double = 0.25
    ) : this(startPose, startHeading, null, null, constraints, start, resolution)

    /**
     * Create a builder from an active trajectory. This is useful for interrupting a live trajectory and smoothly
     * transitioning to a new one.
     */
    @JvmOverloads
    constructor(
            trajectory: Trajectory,
            t: Double,
            constraints: TrajectoryConstraints,
            resolution: Double = 0.25
    ) : this(null, null, trajectory, t, constraints, zeroPosition(trajectory.profile[t]), resolution)

    override fun buildTrajectory(
            path: Path,
            temporalMarkers: List<RelativeTemporalMarker>,
            spatialMarkers: List<SpatialMarker>
    ): Trajectory {
        val goal = MotionState(path.length(), 0.0, 0.0)
        return TrajectoryGenerator.generateTrajectory(
                path,
                constraints,
                start,
                goal,
                temporalMarkers,
                spatialMarkers,
                resolution
        )
    }

    /////////////////////////////////
    ///////////////////////////////
    constructor(
            startPose: Pose2d = DriveMovement.roadRunnerPose2dRaw,
            reversed: Boolean = false
    ) : this(
            if (reversed)
                Pose2d(startPose.x, startPose.y, startPose.heading + PI)
            else
                startPose,
            startPose.heading,
            RoadRunnerConstraints.mecanumConstraints
    )

    override fun lineTo(position: Vector2d): SmartTrajectoryBuilder {
        super.lineTo(position.checkMirror)
        return this
    }

    override fun lineToConstantHeading(position: Vector2d): SmartTrajectoryBuilder {
        super.lineToConstantHeading(position.checkMirror)
        return this
    }

    override fun lineToLinearHeading(position: Vector2d, heading: Double): SmartTrajectoryBuilder {
        super.lineToLinearHeading(position.checkMirror, heading.checkMirror)
        return this
    }

    override fun lineToSplineHeading(position: Vector2d, heading: Double): SmartTrajectoryBuilder {
        super.lineToSplineHeading(position.checkMirror, heading.checkMirror)
        return this
    }

    override fun splineTo(pose: Pose2d): SmartTrajectoryBuilder {
        super.splineTo(pose.checkMirror)
        return this
    }

    override fun splineToConstantHeading(pose: Pose2d): SmartTrajectoryBuilder {
        super.splineToConstantHeading(pose.checkMirror)
        return this
    }

    override fun splineToLinearHeading(pose: Pose2d, heading: Double): SmartTrajectoryBuilder {
        super.splineToLinearHeading(pose.checkMirror, heading.checkMirror)
        return this
    }

    override fun splineToSplineHeading(pose: Pose2d, heading: Double): SmartTrajectoryBuilder {
        super.splineToSplineHeading(pose.checkMirror, heading.checkMirror)
        return this
    }

    override fun strafeLeft(distance: Double): SmartTrajectoryBuilder {
        super.strafeLeft(distance.checkMirror)
        return this
    }

    override fun strafeRight(distance: Double): SmartTrajectoryBuilder {
        super.strafeRight(distance.checkMirror)
        return this
    }

    override fun strafeTo(position: Vector2d): SmartTrajectoryBuilder {
        super.strafeTo(position.checkMirror)
        return this
    }

    override fun addMarker(point: Vector2d, callback: () -> Unit): SmartTrajectoryBuilder {
        super.addMarker(point.checkMirror, callback)
        return this
    }

    fun start() {
        build().start()
    }
}

val Vector2d.checkMirror get() = if (ALLIANCE.isRed()) this else Vector2d(x, -y)
val Pose2d.checkMirror get() = if (ALLIANCE.isRed()) this else Pose2d(x, -y, -heading)
val Double.checkMirror get() = if (ALLIANCE.isRed()) this else -this