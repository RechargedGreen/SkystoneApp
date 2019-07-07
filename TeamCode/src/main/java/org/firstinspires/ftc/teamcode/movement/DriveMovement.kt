package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.odometry.Odometry

object DriveMovement {
    lateinit var odometer: Odometry

    var path = Path()

    fun followSetPath() = path.follow()

    // don't set world variables directly in programs
    var world_x = 0.0
    var world_y = 0.0

    var world_angle = Angle(0.0, 0.0)

    var movement_y = 0.0
    var movement_x = 0.0
    var movement_turn = 0.0

    fun stopDrive() {
        movement_x = 0.0
        movement_y = 0.0
        movement_turn = 0.0
    }

    fun setPosition(x: Double, y: Double, angle_rad: Double) {
        odometer.setPosition(x, y, angle_rad)
    }
}