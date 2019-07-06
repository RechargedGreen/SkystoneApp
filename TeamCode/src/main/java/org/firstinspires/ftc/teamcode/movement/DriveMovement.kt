package org.firstinspires.ftc.teamcode.movement

object DriveMovement {
    var path = Path()

    fun followSetPath() = path.follow()

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
}