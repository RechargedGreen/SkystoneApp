package org.firstinspires.ftc.teamcode.leaguebot.opmode

import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

class ToPosTest : LeagueBotAutoBase() {
    enum class stages {
        s1,
        s2,
        s3,

        stopForTime
    }

    override fun onMainLoop() {
        if (changedStage)
            MovementAlgorithms.initAll()
        val currentStage = stages.values()[stage]

        combinedPacket.put("currentStage", currentStage)

        when (currentStage) {
            stages.s1          -> {
                if (PointControllers.slipAdjustedTarget(0.0, 0.0, 10.0).hypot < 2.0)
                    nextStage()
            }

            stages.s2          -> {
                if (PointControllers.slipAdjustedTarget(0.0, 0.0, 10.0).hypot < 2.0)
                    nextStage()
            }

            stages.s3          -> {
                if (PointControllers.slipAdjustedTarget(0.0, 0.0, 10.0).hypot < 2.0)
                    nextStage(0)
            }

            stages.stopForTime -> {
                stopDrive()
                timeoutStage(2.0, 0)
            }
        }
        if (driver.b.justPressed)
            nextStage(stages.stopForTime.ordinal)
    }
}