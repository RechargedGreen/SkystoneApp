package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive

abstract class LeagueFoundationAuto(alliance: Alliance) : LeagueBotAutoBase(alliance) {
    enum class progStates {
        grab,
        pull,
        wait,
        park,
        doNothing
    }

    var parkAtEnd = true

    val pullSpeed = 1.0

    val parkTime = 3.0

    override fun onInitLoop() {
        telemetry.addData("parkingAtEnd: y to toggle", parkAtEnd)
        if (driver.y.justPressed)
            parkAtEnd = !parkAtEnd
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)

        when (currentStage) {
            progStates.grab      -> {

            }

            progStates.pull      -> {
                LeagueBot.foundationGrabber.grab()
                stopDrive()
                if (stageTimer.seconds() > 0.25)
                    moveFieldCentric(if (ALLIANCE.isRed()) pullSpeed else -pullSpeed, 0.0, 0.0)

                timeoutStage(3.0)
            }

            progStates.wait      -> {
                stopDrive()

                LeagueBot.foundationGrabber.release()
                if (secondsTillEnd < parkTime)
                    nextStage()
            }

            progStates.park      -> {

            }

            progStates.doNothing -> {
                stopDrive()
            }
        }
    }
}

class RedLeagueFoundation : LeagueFoundationAuto(Alliance.RED)
class BlueLeagueFoundation : LeagueFoundationAuto(Alliance.BLUE)

