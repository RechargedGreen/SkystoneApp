package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.qualcomm.robotcore.util.Range.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import kotlin.math.*

object AngleControllers {
    const val wriggleMin = 0.1

    private var wriggleClockwise = true

    fun init() {

    }

    fun wrigProg() {

    }

    fun wriggle(base_deg: Double, width_deg: Double, maxSpeed: Double, minSpeed: Double = wriggleMin, kP: Double = Double.NaN) {
        val world_deg = world_angle.deg
        val wriggle_deg = world_deg - base_deg

        if (wriggle_deg > width_deg)
            wriggleClockwise = false
        if (wriggle_deg < -width_deg)
            wriggleClockwise = true

        var power = maxSpeed

        if (!kP.isNaN()) {
            val target = if (wriggleClockwise) width_deg else -width_deg
            val absToTarget = (target - wriggle_deg).absoluteValue
            power = absToTarget * kP
        }

        power = clip(power, minSpeed, maxSpeed)

        movement_turn = if (wriggleClockwise) power else -power
    }
}