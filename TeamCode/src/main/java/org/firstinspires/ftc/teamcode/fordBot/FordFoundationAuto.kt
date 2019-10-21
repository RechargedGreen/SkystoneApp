package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class FordFoundationAuto(alliance: Alliance) : FordBot(alliance, 90.0) {
    final override fun run() {
        drive.driveInches(-54.0, 90.0)
        drive.turnTo(0.0)
    }
}

@Autonomous
class FordRedFoundation : FordFoundationAuto(Alliance.RED)

@Autonomous
class FordBlueFoundation : FordFoundationAuto(Alliance.BLUE)

@Autonomous
class FordNavigation : FordBot(Alliance.RED, 0.0){
    override fun run() {
        extension.power = 1.0
        delay(1.0)
        extension.power = 0.0
    }
}