package org.firstinspires.ftc.teamcode.sharedhardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.lib.Globals.mode

@Config
class OdometryPuller {
    companion object {
        @JvmField
        var up = 0.63
        @JvmField
        var down = 0.0
    }

    private val servo = RevHubServo("odometryPuller")
    fun update() {
        if (mode.isAutonomous)
            servo.position = down
        else if (mode.isStarted)
            servo.position = up
    }
}