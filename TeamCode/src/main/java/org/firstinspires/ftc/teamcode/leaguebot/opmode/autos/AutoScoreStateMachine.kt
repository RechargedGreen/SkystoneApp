package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

object AutoScoreStateMachine {
    private var movingFoundation = false
    private var hasGrabbedFoundation = false

    private var startedExtendYet = false

    fun start() {
        movingFoundation = !Foundation.hasBeenMoved
        Foundation.hasBeenMoved = true

        startedExtendYet = false

        AutoDump.insertStone()
        RoadRunner.setTrajectories {
            arrayListOf()
        }
    }

    fun update(): Boolean {
        if (movingFoundation) {
            if (world_y_mirror > 0.0)
                tryExtending()
            if (!hasGrabbedFoundation && RoadRunner.done) {
                /*RoadRunnerPaths.startFresh()
                        .spline(40.0, 24.0, 180.0)
                        .back(24.0)
                        .callback { LeagueBot.foundationGrabber.release() }
                        .back(24.0 + 6.0)
                        .build()*/
                hasGrabbedFoundation = true
            }

        } else {
            tryExtending()
            if (AutoDump.isDone)
                return true
        }
        return false
    }

    fun tryExtending() {
        if (!startedExtendYet)
            AutoDump.startExtending()
        startedExtendYet = true
    }
}