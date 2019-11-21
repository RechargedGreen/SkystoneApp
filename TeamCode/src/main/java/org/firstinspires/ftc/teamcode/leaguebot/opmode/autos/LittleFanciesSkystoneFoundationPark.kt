package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.field.Stone
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.MainIntake
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import kotlin.math.absoluteValue

abstract class LittleFanciesSkystoneFoundationPark(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(72.0 - 9.0, -24.0 - 9.0, (-90.0).toRadians)) {
    enum class progStages {
        lineUpParrallel,
        driveIntoQuarry,
        intake,

        pullOutOfQuarry,

        crossField,

        grab,
        pull,
        wait,

        doNothing
    }

    var stoneY = 0.0
    var distanceFromStone = 5.0
    var distanceFromNextStone = 3.0

    val halfStoneWidth = Stone.LENGTH / 2.0

    val needleX = 39.0 // needle allows 6" on either side

    override fun onStart() {
        stoneY = Quarry.popStone().center_y
    }

    override fun onInitLoop() {
        telemetry.addData("skystoneIndex", SkystoneDetector.placeInt)
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        telemetry.addData("skystoneIndex", SkystoneDetector.placeInt)
        telemetry.addData("currentStage", currentStage)

        stopDrive()
        LeagueBot.intake.state = MainIntake.State.STOP

        when (currentStage) {
            progStages.lineUpParrallel -> {
                val r = goToPosition_mirror(40.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
                if (r.point.hypot < 2.0 && r.deg.absoluteValue < 2.0)
                    nextStage()
            }
            progStages.driveIntoQuarry -> {
                val r = goToPosition_mirror(22.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
                if (r.point.hypot < 2.0 && r.deg.absoluteValue < 2.0)
                    nextStage()
            }

            progStages.intake -> {
                LeagueBot.intake.state = MainIntake.State.IN
                if (isTimedOut(0.25))
                    moveFieldCentric_mirror(0.0, -0.25, 0.0)
                pointAngle_mirror(180.0)
                if (world_y_mirror < stoneY + 9.0 - halfStoneWidth + distanceFromNextStone)
                    stopDrive()
                if (isTimedOut(2.0))
                    nextStage()
            }

            progStages.pullOutOfQuarry -> {
                val r = goToPosition_mirror(needleX, stoneY + 5.0, 90.0)
                if (r.point.x.absoluteValue < 2.0 && r.deg.absoluteValue < 3.0)
                    nextStage()
            }

            progStages.crossField -> {
                val r = goToPosition_mirror(needleX, 48.0, 90.0)
                ScorerState.triggerGrab()
                if(world_y_mirror > 24.0) {
                    ScorerState.triggerExtend()
                    LeagueBot.foundationGrabber.prepForGrab()
                }
                if(r.point.hypot < 3.0)
                    nextStage()
            }

            progStages.grab -> {
                ScorerState.triggerExtend()
                moveFieldCentric_mirror(-0.2, 0.0, 0.0)
                pointAngle_mirror(90.0)
                if(world_x_mirror < 21 + 9.0)
                    nextStage()
            }

            progStages.pull -> {
                ScorerState.triggerRelease()
                LeagueBot.foundationGrabber.grab()
                if(isTimedOut(.25)) {
                    moveFieldCentric_mirror(1.0, 0.1, 0.0)
                    pointAngle_mirror(90.0)
                }
                if(isTimedOut(.75))
                    ScorerState.triggerPullBack()
                if(isTimedOut(2.0))
                    nextStage()
            }

            progStages.wait -> {
                ScorerState.triggerLoad()
                if(isTimedOut(0.5))
                    nextStage()
            }

            progStages.doNothing -> {
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }
        }
    }
}

@Autonomous
class Red_LittleFanciesSkystoneFoundationPark : LittleFanciesSkystoneFoundationPark(Alliance.RED)

@Autonomous
class Blue_LittleFanciesSkystoneFoundationPark : LittleFanciesSkystoneFoundationPark(Alliance.BLUE)