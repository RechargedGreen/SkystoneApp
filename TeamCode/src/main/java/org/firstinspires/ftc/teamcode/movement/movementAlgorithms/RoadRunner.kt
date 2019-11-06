package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.acmerobotics.dashboard.config.*
import com.acmerobotics.roadrunner.control.*
import com.acmerobotics.roadrunner.drive.*
import com.acmerobotics.roadrunner.followers.*
import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.path.heading.*
import com.acmerobotics.roadrunner.profile.*
import com.acmerobotics.roadrunner.trajectory.*
import com.acmerobotics.roadrunner.trajectory.constraints.*
import org.firstinspires.ftc.teamcode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.Interpolators.tangent
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

object RoadRunner {
    val trajectoryFollower = HolonomicPIDVAFollower(RoadRunnerConstants.TRANSLATIONAL_PID, RoadRunnerConstants.TRANSLATIONAL_PID, RoadRunnerConstants.HEADING_PID)
    val turnController = PIDFController(RoadRunnerConstants.HEADING_PID)

    private lateinit var turnProfile: MotionProfile
    private var turnStart = 0.0

    fun reset() {
        state = State.IDLE
    }

    enum class State {
        IDLE,
        TURN,
        TRAJECTORY
    }

    val done get() = state == State.IDLE
    var state = State.IDLE

    val lastError
        get() = when (state) {
            State.TRAJECTORY -> trajectoryFollower.lastError
            State.TURN       -> Pose2d(0.0, 0.0, turnController.lastError)
            State.IDLE       -> Pose2d()
        }

    var turn_deg: Double = 0.0
        set(value) {
            val t_rad = turn_deg.toRadians * RunData.ALLIANCE.sign
            val c_rad = world_angle_unwrapped_raw.rad
            turnProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                    MotionState(c_rad, 0.0),
                    MotionState(t_rad, 0.0),
                    RoadRunnerConstants.maxAngVelRad,
                    RoadRunnerConstants.maxAngAccelRad
            )
            turnStart = Clock.seconds
            state = State.TURN
            stopOnEnd = true
            field = value
        }

    fun setTrajectory(trajectory: Trajectory) {
        state = State.TRAJECTORY
        trajectoryFollower.followTrajectory(trajectory)
    }

    var stopOnEnd = true

    fun update() {
        when (state) {
            State.TURN       -> {
                val t = Clock.seconds - turnStart
                val targetState = turnProfile[t]

                val targetOmega = targetState.v

                turnController.targetPosition = targetState.x
                val correction = turnController.update(world_angle_unwrapped_raw.rad, targetOmega)

                RoadRunnerConstants.applySignal(DriveSignal(
                        Pose2d(0.0, 0.0, targetOmega + correction)
                ))

                if (t >= turnProfile.duration())
                    setIdle()
            }

            State.TRAJECTORY -> {
                val pose = DriveMovement.roadRunnerPose2dRaw
                RoadRunnerConstants.applySignal(trajectoryFollower.update(pose))

                val trajectory = trajectoryFollower.trajectory

                Globals.fieldOverlay.drawSampledPath(trajectory.path)

                Globals.fieldOverlay.setStroke("#3F51B5")
                Globals.fieldOverlay.fillCircle(pose.x, pose.y, 3.0)

                if (!trajectoryFollower.isFollowing())
                    setIdle()
            }
        }

        val error = lastError
        Globals.packet.put("xError", error.x)
        Globals.packet.put("yError", error.y)
        Globals.packet.put("degError", error.heading.toDegrees)
    }

    fun setIdle() {
        state = State.IDLE
        if (stopOnEnd)
            stopDrive()
    }
}


object RoadRunnerPaths {
    var startAngle = 0.0
    lateinit var trajectoryBuilder: TrajectoryBuilder

    fun startFresh(): RoadRunnerPaths {
        startAngle = world_angle_mirror.deg
        trajectoryBuilder = TrajectoryBuilder(DriveMovement.roadRunnerPose2dRaw, RoadRunnerConstants.constraints)
        return this
    }

    fun startInterrupted(): RoadRunnerPaths {
        startAngle = world_angle_mirror.deg
        trajectoryBuilder = TrajectoryBuilder(
                RoadRunner.trajectoryFollower.trajectory,
                RoadRunner.trajectoryFollower.elapsedTime(),
                RoadRunnerConstants.constraints
        )
        return this
    }

    fun spline(x: Double, y: Double, deg: Double, interpolater: HeadingInterpolator = tangent): RoadRunnerPaths {
        trajectoryBuilder.splineTo(Pose(x, y, deg.toRadians).checkMirror.toRoadRunner, interpolater)
        return this
    }

    fun lineTo(x: Double, y: Double, headingInterpolator: HeadingInterpolator = tangent): RoadRunnerPaths {
        trajectoryBuilder.lineTo(Point(x, y).checkMirror.toRoadRunner, headingInterpolator)
        return this
    }

    fun lineTo(x: Double, y: Double): RoadRunnerPaths {
        trajectoryBuilder.strafeTo(Point(x, y).checkMirror.toRoadRunner)
        return this
    }

    fun setReversed(reversed: Boolean): RoadRunnerPaths {
        trajectoryBuilder.setReversed(reversed)
        return this
    }

    fun forward(inches: Double): RoadRunnerPaths {
        trajectoryBuilder.forward(inches)
        return this
    }

    fun back(inches: Double): RoadRunnerPaths {
        trajectoryBuilder.back(inches)
        return this
    }

    fun left(inches: Double): RoadRunnerPaths {
        if (ALLIANCE.isRed())
            trajectoryBuilder.strafeLeft(inches)
        else
            trajectoryBuilder.strafeRight(inches)
        return this
    }

    fun strafeRight(inches: Double): RoadRunnerPaths {
        if (ALLIANCE.isRed())
            trajectoryBuilder.strafeRight(inches)
        else
            trajectoryBuilder.strafeLeft(inches)
        return this
    }

    fun callback(callback: () -> Unit): RoadRunnerPaths {
        trajectoryBuilder.addMarker(callback)
        return this
    }

    fun callback(time: Double, callback: () -> Unit): RoadRunnerPaths {
        trajectoryBuilder.addMarker(time, callback)
        return this
    }

    fun callback(x: Double, y: Double, callback: () -> Unit): RoadRunnerPaths {
        trajectoryBuilder.addMarker(Point(x, y).toRoadRunner, callback)
        return this
    }

    fun startSmart(): RoadRunnerPaths {
        if (RoadRunner.trajectoryFollower.isFollowing() && RoadRunner.state == RoadRunner.State.IDLE)
            startInterrupted()
        else
            startFresh()
        return this
    }

    fun build() {
        RoadRunner.setTrajectory(trajectoryBuilder.build())
    }
}

@Config
object RoadRunnerConstants {
    const val WHEEL_DIAMETER = 100.0 / 25.4
    const val MAX_RPM = 312.0
    const val kV = (MAX_RPM / 60.0) * WHEEL_DIAMETER * PI

    val constraints get() = MecanumConstraints(DriveConstraints(maxVel, maxAccel, 0.0, maxAngVelRad, maxAngAccelRad, 0.0), trackWidth)

    @JvmField
    var trackWidth = 0.0

    @JvmField
    var TRANSLATIONAL_PID = PIDCoefficients(0.0, 0.0, 0.0)

    @JvmField
    var HEADING_PID = PIDCoefficients(0.0, 0.0, 0.0)

    @JvmField
    var maxAccel = 30.0
    @JvmField
    var maxVel = 30.0
    @JvmField
    var maxAngVelDeg = 180.0
    @JvmField
    var maxAngAccelDeg = 180.0
    val maxAngVelRad get() = maxAngVelDeg.toRadians
    val maxAngAccelRad = maxAngAccelDeg.toRadians

    fun applySignal(driveSignal: DriveSignal) {
        val vel = driveSignal.vel.toNormal
        movement_x = vel.x * kV
        movement_y = vel.y * kV
        movement_turn = vel.rad * trackWidth * kV
    }
}

object Interpolators {
    val tangent get() = TangentInterpolator()
    fun wiggle(amplitude: Double, desiredPeriod: Double, baseInterpolator: HeadingInterpolator = tangent) = WiggleInterpolator(amplitude, desiredPeriod, baseInterpolator)
    fun spline(startHeading: Double, endHeading: Double) = SplineInterpolator(startHeading.toRadians.checkMirror, endHeading.toRadians.checkMirror)
    fun linear(startHeading: Double, angle: Double) = LinearInterpolator(startHeading.toRadians.checkMirror, angle.toRadians.checkMirror)
    fun constant(heading: Double) = ConstantInterpolator(heading.toRadians.checkMirror)
}