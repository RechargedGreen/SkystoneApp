package org.firstinspires.ftc.teamcode.odometry

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*

object ThreeWheel : Odometry {
    // last encoder positions
    private var last_l_encoder = 0
    private var last_r_encoder = 0
    private var last_a_encoder = 0

    // used for reading angle absolutely not integrated
    private var leftInitialReading = 0
    private var rightInitialReading = 0
    private var lastResetAngle = 0.0

    fun update(curr_l_encoder: Int, curr_r_encoder: Int, curr_a_encoder: Int, provider: ThreeWheelProvider) {
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
        val final_angle_rad = ((leftTotal - rightTotal) / turnTrackWidth) + lastResetAngle

        // the aux wheel moves when we rotate, so cancel this out with a prediction
        val aux_prediction = angleIncrement * auxTrackWidth
        val r_xDistance = a_wheel_delta - aux_prediction

        val r_yDistance = (l_wheel_delta + r_wheel_delta) / 2.0

        DriveMovement.updatePos(Pose(r_xDistance, r_yDistance, angleIncrement), Angle.createRad(final_angle_rad))

        last_l_encoder = curr_l_encoder
        last_r_encoder = curr_r_encoder
        last_a_encoder = curr_a_encoder
    }


    override fun setAngleRad(angle_rad: Double) {
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