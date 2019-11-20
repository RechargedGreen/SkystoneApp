package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.MainIntake
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians

abstract class LittleFanciesSkystoneFoundationPark(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - 9.0, -24.0 - 9.0, (-90.0).toRadians)) {
    enum class progStages {
        gettingPastPartner,
        lineUpParrallel,
        driveIntoQuarry,
        intake,

        doNothing
    }

    var stoneX = 0.0
    var distanceFromStone = 3.0
    var distanceFromNextStone = 3.0

    val halfStoneWidth = 4.0

    override fun onStart() {
        stoneX = Quarry.popStone().center_x
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        stopDrive()
        LeagueBot.foundationGrabber.release()
        LeagueBot.intake.state = MainIntake.State.STOP

        when (currentStage) {
            progStages.gettingPastPartner -> {
                moveFieldCentric_mirror(-0.5, 0.0, 0.0)
                if (world_x_mirror < 72.0 - 18.0 - 9.0)
                    nextStage()
            }
            progStages.lineUpParrallel -> {
                val r = goToPosition_mirror(36.0, stoneX + halfStoneWidth + distanceFromStone, 180.0)
                if (r.point.hypot < 2.0 && r.deg < 2.0)
                    nextStage()
            }
            progStages.driveIntoQuarry -> {
                val r = goToPosition_mirror(22.0, stoneX - halfStoneWidth + distanceFromStone, 180.0)
                if (r.point.hypot < 2.0 && r.deg < 2.0)
                    nextStage()
            }

            progStages.intake -> {
                moveFieldCentric_mirror(0.0, 0.25, 0.0)
                pointAngle_mirror(180.0)
                if (world_y_mirror < stoneX + 9.0 - 2.0 + distanceFromNextStone)
                    stopDrive()
                if (isTimedOut(2.0))
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