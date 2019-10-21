package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.lib.*

@TeleOp
class FordPIDTuning : FordBot(Alliance.RED, 0.0) {
    override fun run() {
        drive.driveInches(100.0, 0.0)
        delay(Double.NaN)
    }
}