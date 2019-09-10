package org.firstinspires.ftc.teamcode.odometry

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*

object ThreeWheel : Odometry {
    // last encoder positions
    private var last_l_encoder = 0
    private var last_r_encoder = 0
    private var last_a_encoder = 0

    // used for reading angle absolutely not integrated
    var angleRadBias = 0.0

    private var lastRawAngle = 0.0

    fun update(curr_l_encoder: Int, curr_r_encoder: Int, curr_a_encoder: Int, leftInchesPerTick: Double, rightInchesPerTick: Double, auxInchesPerTick: Double, turnTrackWidth: Double, auxTrackWidth: Double) {
        DriveMovement.odometer = this

        val lWheelDelta = (curr_l_encoder - last_l_encoder) * leftInchesPerTick
        val rWheelDelta = (curr_r_encoder - last_r_encoder) * rightInchesPerTick
        val aWheelDelta = (curr_a_encoder - last_a_encoder) * auxInchesPerTick


        // calculate angle change for running arc integration and aux prediction
        val angleIncrement = (lWheelDelta - rWheelDelta) / turnTrackWidth

        // use absolute for actual angle
        val leftTotal = curr_l_encoder * rightInchesPerTick
        val rightTotal = curr_r_encoder * rightInchesPerTick
        lastRawAngle = ((leftTotal - rightTotal) / turnTrackWidth)
        val finalAngleRad = lastRawAngle + angleRadBias

        // the aux wheel moves when we rotate, so cancel this out with a prediction
        val aux_prediction = angleIncrement * auxTrackWidth

        DriveMovement.updatePos(Pose(aWheelDelta - aux_prediction, (lWheelDelta + rWheelDelta) / 2.0, angleIncrement), Angle.createUnwrappedRad(finalAngleRad))

        last_l_encoder = curr_l_encoder
        last_r_encoder = curr_r_encoder
        last_a_encoder = curr_a_encoder
    }

    override fun setAngleRad(angle_rad: Double) {
        angleRadBias = angle_rad - lastRawAngle
    }

    fun addAngleRad(angle_rad: Double) {
        angleRadBias += angle_rad
    }

    override fun addAngleBias(angle_rad: Double) {
        angleRadBias += angle_rad
    }
}