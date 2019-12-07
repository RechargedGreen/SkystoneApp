package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.field.Stone
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot
import org.firstinspires.ftc.teamcode.leaguebot.hardware.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import kotlin.math.absoluteValue

abstract class SingleSkystone_Foundation_Rotation(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(72.0 - 9.0, -24.0 - 9.0, (-90.0).toRadians)) {
    enum class progStages {
        parkPartner,

        lineUpParrallel,
        driveIntoQuarry,
        intake,

        pullOutOfQuarry,

        crossField,

        grab,
        pull,
        rotate,
        slam,

        moveUp,
        park,

        doNothing
    }

    var stoneY = 0.0
    var distanceFromStone = 5.0
    var distanceFromNextStone = 3.0

    val halfStoneWidth = Stone.LENGTH / 2.0

    val needleX = 39.0 // needle allows 6" on either side // 39.0 was tested

    override fun onStart() {
        stoneY = Quarry.popStone().center_y
    }

    override fun onInitLoop() {
        telemetry.addData("skystoneIndex", SkystoneDetector.placeInt)
        if (driver.x.justPressed)
            parkPartner = !parkPartner
        telemetry.addData("x to toggle park partner", parkPartner)
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        telemetry.addData("x", world_x_mirror)
        telemetry.addData("y", world_y_mirror)
        telemetry.addData("deg", world_angle_mirror.deg)

        telemetry.addData("skystoneIndex", SkystoneDetector.placeInt)
        telemetry.addData("currentStage", currentStage)

        stopDrive()
        Robot.intake.state = MainIntake.State.STOP

        when (currentStage) {
            progStages.parkPartner -> {
                if (parkPartner) {
                    moveFieldCentric_mirror(if (world_x_mirror > 48.0) -0.25 else 0.0, 1.0, 0.0)
                    if (isTimedOut(2.0) || world_y_mirror > -10.0)
                        nextStage()
                } else {
                    nextStage()
                }
            }

            progStages.lineUpParrallel -> {
                val r = goToPosition_mirror(40.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
                if (r.point.hypot < 2.0 && r.deg.absoluteValue < 2.0)
                    nextStage()
            }
            progStages.driveIntoQuarry -> {
                val r = goToPosition_mirror(23.0 + if (ALLIANCE.isRed()) 0.0 else 2.0, stoneY + halfStoneWidth + distanceFromStone + 9.0, 180.0)
                if (r.point.hypot < 2.0 && r.deg.absoluteValue < 2.0 && isTimedOut(3.0))
                    nextStage()
            }

            progStages.intake -> {
                Robot.intake.state = MainIntake.State.IN
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
                if (r.point.x.absoluteValue < 2.0 && r.deg.absoluteValue < 3.0 && isTimedOut(2.0))
                    nextStage()
            }

            progStages.crossField -> {
                val r = goToPosition_mirror(needleX, if (ALLIANCE.isRed()) 48.0 else 42.0, 90.0, yClip = 1.0)
                ScorerState.triggerGrab()
                if (world_y_mirror > 24.0) {
                    ScorerState.triggerExtend()
                    Robot.foundationGrabber.prepForGrab()
                }
                if (r.point.hypot < 3.0)
                    nextStage()
            }

            progStages.grab -> {
                ScorerState.triggerExtend()
                moveFieldCentric_mirror(-0.2, 0.0, 0.0)
                pointAngle_mirror(90.0)
                if (world_x_mirror < 21 + 9.0)
                    nextStage()
            }

            progStages.pull -> {
                ScorerState.triggerRelease()
                Robot.foundationGrabber.grab()
                if (isTimedOut(.25)) {
                    moveFieldCentric_mirror(1.0, 0.0, 0.0)
                    pointAngle_mirror(90.0)
                }
                if (isTimedOut(.75))
                    ScorerState.triggerPullBack()
                if (world_x_mirror > 45.0) {
                    ScorerState.triggerPullBack()
                    nextStage()
                }
            }

            progStages.rotate -> {
                if (pointAngle_mirror(180.0) < 5.0)
                    nextStage()
                movement_turn = 1.0 * ALLIANCE.sign
            }

            progStages.slam -> {
                moveFieldCentric_mirror(1.0, 0.5, 0.0)
                pointAngle_mirror(180.0)
                if (isTimedOut(1.0)) {
                    Robot.foundationGrabber.release()
                    if (isTimedOut(1.25))
                        nextStage()
                }
            }

            progStages.moveUp -> {
                val r = goToPosition_mirror(36.0, 14.0, 180.0)
                if (r.point.hypot > 3.0)
                    nextStage()
            }

            progStages.park -> {
                val r = goToPosition_mirror(36.0, 0.0, 180.0)
                if (r.deg > 5.0) {
                    movement_y = 0.0
                    movement_x = 0.0
                }
                if ((r.point.hypot < 3.0 && r.deg < 3.0) || isTimedOut(2.0))
                    nextStage()
            }

            progStages.doNothing -> {
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }
        }
    }

    var parkPartner = false
}

@Autonomous
class Red_SingleSkystone_Foundation_Rotation : SingleSkystone_Foundation_Rotation(Alliance.RED)

@Autonomous
class Blue_SingleSkystone_Foundation_Rotation : SingleSkystone_Foundation_Rotation(Alliance.BLUE)