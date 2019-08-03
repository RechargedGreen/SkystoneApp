package org.firstinspires.ftc.teamcode.odometry

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*

object TwoWheel : Odometry {
    lateinit var provider: TwoWheelProvider

    private var last_y_encoder = 0
    private var last_x_encoder = 0

    private var angleBias = 0.0
    private var last_raw_angle_rad = 0.0

    fun update(curr_y_encoder: Int, curr_x_encoder: Int, curr_raw_angle_rad: Double) {
        val yTrackWidth = provider.yTrackWidth()
        val xTrackWidth = provider.xTrackWidth()
        val yTicksPerInch = provider.yTicksPerInch()
        val xTicksPerInch = provider.xTicksPerInch()

        // convert ticks to wheel deltas
        val xEncoderDelta = curr_x_encoder - last_x_encoder
        val yEncoderDelta = curr_y_encoder - last_y_encoder
        val xWheelDelta = xEncoderDelta / xTicksPerInch
        val yWheelDelta = yEncoderDelta / yTicksPerInch

        // determine angle increment for circle arc and wheel predictions
        val angleIncrement = curr_raw_angle_rad - last_raw_angle_rad

        // angle we set world_angle to
        val final_angle_rad = curr_raw_angle_rad + angleBias

        // cancel out a prediction on the wheel readings based on angle increment
        val xPrediction = angleIncrement * xTrackWidth
        val yPrediction = angleIncrement * yTrackWidth

        val r_x = xWheelDelta - xPrediction
        val r_y = yWheelDelta - yPrediction

        DriveMovement.updatePos(Pose(r_x, r_y, angleIncrement), Angle.createUnwrappedRad(final_angle_rad))

        last_x_encoder = curr_x_encoder
        last_y_encoder = curr_y_encoder
        last_raw_angle_rad = curr_raw_angle_rad
    }

    override fun setAngleRad(angle_rad: Double) {
        angleBias = angle_rad - last_raw_angle_rad
    }
}

interface TwoWheelProvider {
    fun yTrackWidth(): Double
    fun xTrackWidth(): Double
    fun xTicksPerInch(): Double
    fun yTicksPerInch(): Double
}