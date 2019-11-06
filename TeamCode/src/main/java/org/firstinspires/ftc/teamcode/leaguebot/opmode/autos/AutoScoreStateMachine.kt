package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

object AutoScoreStateMachine {
    fun start() {
        AutoDump.insertStone()
        RoadRunnerPaths.startFresh()
                .reverse()
                .spline(0.0, 0.0, 0.0)
                .spline(0.0, 0.0, 0.0)
                .callback { AutoDump.startDumping() }
    }

    fun update(): Boolean {
        if (world_x_mirror > 0.0)
            AutoDump.startExtending()
        return AutoDump.isDone
    }
}