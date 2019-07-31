package org.firstinspires.ftc.teamcode.roverBot.opMode

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.roverBot.hardware.*

@TeleOp
class RoverTeleOp : RoverTeleOpBase() {
    var hangMode = false

    override fun onMainLoop() {
        val left = driver.leftY
        val right = driver.rightY
        RoverMovement.move((left + right) / 2.0, (left - right) / 2.0)

        val tryDumping = driver.leftBumper
        val callFeed = driver.rightBumper
        val manual = driver.rightTrigger - driver.leftTrigger
        val intake = driver.leftBumper

        if (driver.dUp || driver.dRight || driver.dDown || driver.dLeft)
            hangMode = true

        RoverBot.autoFeed.setDisabled(hangMode)

        if (callFeed)
            RoverBot.autoFeed.callNormalFeed()

        if (RoverBot.autoFeed.doneWithAutoFeed()) {
            if (intake) {
                RoverBot.intake.collect()
                RoverBot.flip.flipDown()
            } else {
                RoverBot.intake.stop()
                RoverBot.flip.flipUp()
            }
            if (hangMode) {
                RoverBot.lift.manual(manual)
                RoverBot.extension.retract()
            } else {
                RoverBot.extension.manual(manual)
                RoverBot.lift.lower()
            }
        }
    }
}