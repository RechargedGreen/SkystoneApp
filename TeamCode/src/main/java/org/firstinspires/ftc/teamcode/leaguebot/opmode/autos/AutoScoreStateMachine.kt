package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.*
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
        RoadRunnerPaths.startFresh()

        if (movingFoundation)
            RoadRunnerPaths
                    .lineTo(36.0, -12.0, Interpolators.spline(DriveMovement.world_angle_mirror.heading, 0.0))
                    .spline(36.0, 18.0, 0.0) // clear bumpers of other robot
                    .lineTo(36.0, 48.0, Interpolators.spline(0.0, 90.0))
                    .back(3.0)
                    .callback {
                        AutoDump.startDumping()
                        LeagueBot.foundationGrabber.grab()
                    }
                    .back(3.0)
        else
            RoadRunnerPaths
                    .lineTo(36.0, -12.0, Interpolators.spline(DriveMovement.world_angle_mirror.heading, 0.0))
                    .spline(36.0, 18.0, 0.0) // clear bumpers of other robot
                    .spline(36.0, Field.NORTH_WALL - Foundation.WIDTH - 9.0, 0.0)
                    .callback { AutoDump.startDumping() }

        RoadRunnerPaths.build()
    }

    fun update(): Boolean {
        if (movingFoundation) {
            if (world_y_mirror > 0.0)
                tryExtending()
            if (!hasGrabbedFoundation && RoadRunner.done) {
                RoadRunnerPaths.startFresh()
                        .spline(40.0, 24.0, 180.0)
                        .back(24.0)
                        .callback { LeagueBot.foundationGrabber.release() }
                        .back(24.0 + 6.0)
                        .build()
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