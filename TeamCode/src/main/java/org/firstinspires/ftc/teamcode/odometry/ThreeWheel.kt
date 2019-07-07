package org.firstinspires.ftc.teamcode.odometry

import org.firstinspires.ftc.teamcode.field.Geometry
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.movement.Angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import org.firstinspires.ftc.teamcode.util.MathUtil

object ThreeWheel : Odometry {
    lateinit var provider: ThreeWheelProvider

    private var lastL = 0
    private var lastR = 0
    private var lastA = 0

    var world_x = 0.0
        private set(value) {
            DriveMovement.world_x = value
            field = value
        }
    var world_y = 0.0
        private set(value) {
            DriveMovement.world_x = value
            field = value
        }
    var worldAngle_rad = 0.0
        private set(value) {
            DriveMovement.world_angle = Angle.createRad(value)
            field = value
        }
    var worldAngle_rad_unwrapped = 0.0
        private set(value) {
            field = value
        }

    // last encoder positions
    private var last_l_encoder = 0
    private var last_r_encoder = 0
    private var last_a_encoder = 0

    // used for reading angle absolutely not integrated
    private var leftInitialReading = 0
    private var rightInitialReading = 0
    private var lastResetAngle = 0.0

    fun initiate(lEncoder: Int, rEncoder: Int, aEncoder: Int) {
        last_l_encoder = lEncoder
        last_r_encoder = rEncoder
        last_a_encoder = aEncoder
    }

    fun update(curr_l_encoder: Int, curr_r_encoder: Int, curr_a_encoder: Int) {
        val lastAngle_rad = MathUtil.angleWrap(worldAngle_rad)

        val leftTicksPerInch = provider.leftTicksPerInch()
        val rightTicksPerInch = provider.rightTicksPerInch()
        val auxTicksPerInch = provider.auxTicksPerInch()
        val turnTrackWidth = provider.turnTrackWidth()
        val auxTrackWidth = provider.auxTrackWidth()


        val l_wheel_delta = (curr_l_encoder - last_l_encoder) / leftTicksPerInch
        val r_wheel_delta = (curr_r_encoder - last_r_encoder) / rightTicksPerInch
        val a_wheel_delta = (curr_a_encoder - last_a_encoder) / auxTicksPerInch


        // calculate angle change for running arc integration and aux prediction
        val angleIncrement = (l_wheel_delta - r_wheel_delta) / turnTrackWidth

        // use absolute for actual angle
        val leftTotal = (curr_l_encoder / leftTicksPerInch) - leftInitialReading
        val rightTotal = (curr_r_encoder / rightTicksPerInch) - rightInitialReading
        worldAngle_rad_unwrapped = ((leftTotal - rightTotal) / turnTrackWidth) + lastResetAngle

        // the aux wheel moves when we rotate, so cancel this out with a prediction
        val aux_prediction = angleIncrement * auxTrackWidth
        val r_xDistance = a_wheel_delta - aux_prediction

        var relativeY = (l_wheel_delta + r_wheel_delta) / 2.0
        var relativeX = r_xDistance

        val circleArcDelta = Geometry.circleArcRelativeDelta(Pose(
                relativeX,
                relativeY,
                angleIncrement
        ))

        val finalDelta = Geometry.pointDelta(circleArcDelta, lastAngle_rad)
        world_x += finalDelta.x
        world_y += finalDelta.y

        last_l_encoder = curr_l_encoder
        last_r_encoder = curr_r_encoder
        last_a_encoder = curr_a_encoder
    }


    override fun setPosition(x: Double, y: Double, angle_rad: Double) {
        world_x = x
        world_y = y
        worldAngle_rad_unwrapped = angle_rad

        leftInitialReading = last_l_encoder
        rightInitialReading = last_r_encoder
        lastResetAngle = angle_rad
    }
}

interface ThreeWheelProvider {
    fun turnTrackWidth(): Double
    fun auxTrackWidth(): Double

    fun leftTicksPerInch(): Double
    fun rightTicksPerInch(): Double
    fun auxTicksPerInch(): Double
}