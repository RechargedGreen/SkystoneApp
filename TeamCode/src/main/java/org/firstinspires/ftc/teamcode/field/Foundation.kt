package org.firstinspires.ftc.teamcode.field

import org.firstinspires.ftc.teamcode.field.Field.NORTH_WALL

object Foundation {
    fun reset() {
        center_x = start_center_x
        center_y = start_center_y
    }

    var center_x = 0.0
    var center_y = 0.0

    const val LENGTH = 34.5
    const val WIDTH = 18.5

    const val start_center_x = 24.0 - WIDTH / 2.0
    const val start_center_y = NORTH_WALL - 4.0 - LENGTH / 2.0
}