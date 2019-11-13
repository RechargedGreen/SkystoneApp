package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.acmerobotics.roadrunner.drive.DriveSignal
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.path.heading.*
import com.acmerobotics.roadrunner.profile.MotionProfile
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints
import org.firstinspires.ftc.teamcode.drawSampledPath
import org.firstinspires.ftc.teamcode.field.checkMirror
import org.firstinspires.ftc.teamcode.field.toNormal
import org.firstinspires.ftc.teamcode.lib.Globals
import org.firstinspires.ftc.teamcode.lib.RunData
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.roadRunnerPose2dRaw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.toDegrees
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.util.Clock
import kotlin.contracts.contract
import kotlin.math.PI

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
            State.TURN -> Pose2d(0.0, 0.0, turnController.lastError)
            State.IDLE -> Pose2d()
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

    var index = -1
    var trajectories = ArrayList<Trajectory>()

    fun setTrajectory(trajectory: Trajectory) = setTrajectories { arrayListOf(trajectory) }

    fun setTrajectories(trajectories: () -> ArrayList<Trajectory>) {
        state = State.TRAJECTORY
        this.trajectories = trajectories()
        index = -1
    }

    var stopOnEnd = true

    fun update() {
        when (state) {
            State.TURN -> {
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
                if (index < 0 || !trajectoryFollower.isFollowing()) {
                    index++
                    if (index >= trajectories.size) {
                        setIdle()
                    } else {
                        trajectoryFollower.followTrajectory(trajectories[index])
                    }
                }

                val pose = roadRunnerPose2dRaw
                RoadRunnerConstants.applySignal(trajectoryFollower.update(pose))

                for (trajectory in trajectories)
                    Globals.fieldOverlay.drawSampledPath(trajectory.path)

                Globals.fieldOverlay.setStroke("#3F51B5")
                Globals.fieldOverlay.fillCircle(pose.x, pose.y, 3.0)

            }

            State.IDLE -> {
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

    fun newBuilder(pose: Pose2d = roadRunnerPose2dRaw, constraints: DriveConstraints = RoadRunnerConstants.constraints): AllianceCheckingBuilder {
        return AllianceCheckingBuilder(TrajectoryBuilder(pose, constraints))
    }

    fun interruptedBuilder(constraints: DriveConstraints): AllianceCheckingBuilder {
        return AllianceCheckingBuilder(TrajectoryBuilder(
                trajectoryFollower.trajectory,
                trajectoryFollower.elapsedTime(),
                constraints
        ))
    }
}

@Config
object RoadRunnerConstants {
    const val WHEEL_DIAMETER = 3.937
    const val MAX_RPM = 312.0
    const val kV = 1.0 / (MAX_RPM / 60.0 * WHEEL_DIAMETER * PI)

    val constraints get() = MecanumConstraints(DriveConstraints(maxVel, maxAccel, 0.0, maxAngVelRad, maxAngAccelRad, 0.0), trackWidth)

    @JvmField
    var trackWidth = 17.5

    @JvmField
    var TRANSLATIONAL_PID = PIDCoefficients(0.0, 0.0, 0.0)

    @JvmField
    var HEADING_PID = PIDCoefficients(0.0, 0.0, 0.0)

    @JvmField
    var maxAccel = 60.0
    @JvmField
    var maxVel = 45.0
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