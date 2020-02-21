package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.AutoClaw
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.autoClaw
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance

abstract class ClawTest(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        when {
            driver.leftBumper.currentState -> autoClaw.state = AutoClaw.State.TELEOP
            driver.rightBumper.currentState -> autoClaw.state = AutoClaw.State.VERTICAL
            driver.leftTriggerB.currentState -> autoClaw.state = AutoClaw.State.STOW_STONE
            driver.rightTriggerB.currentState -> autoClaw.state = AutoClaw.State.PRE_GRAB
            driver.b.currentState -> autoClaw.state = AutoClaw.State.GRABBING
        }
    }
}

@TeleOp(group = "c")
class ClawTest_Red : ClawTest(Alliance.RED)

@TeleOp(group = "c")
class ClawTest_Blue : ClawTest(Alliance.RED)