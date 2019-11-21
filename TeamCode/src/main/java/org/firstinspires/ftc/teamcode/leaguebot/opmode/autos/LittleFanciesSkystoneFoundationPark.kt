package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.field.Stone
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.MainIntake
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import kotlin.math.absoluteValue

abstract class LittleFanciesSkystoneFoundationPark(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - 9.0, -24.0 - 9.0, (-90.0).toRadians)) {
    enum class progStages {
        lineUpParrallel,
        driveIntoQuarry,
        intake,

        pullOutOfQuarry,

        doNothing
    }

    var stoneY = 0.0
    var distanceFromStone = 3.0
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
        LeagueBot.foundationGrabber.release()
        LeagueBot.intake.state = MainIntake.State.STOP

        when (currentStage) {
            progStages.lineUpParrallel -> {
                val r = goToPosition_mirror(40.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
                if (r.point.hypot < 2.0 && r.deg.absoluteValue < 2.0)
                    nextStage()
            }
            progStages.driveIntoQuarry -> {
                val r = goToPosition_mirror(23.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
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
                val r = goToPosition_mirror(needleX, stoneY + 5.0, -90.0)
                if (r.point.x.absoluteValue < 2.0 && r.deg.absoluteValue < 3.0)
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