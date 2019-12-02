package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode

@Config
class Capstone {
    companion object {
        @JvmField
        var deploy = 1.0
        @JvmField
        var stash = 0.0
    }

    private val servo = RevHubServo("cap")
    var deployed = false

    fun update() {
        if (mode.movementAllowed)
            servo.position = if (deployed) deploy else stash
    }
}