package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.field.QuarryLocation
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import org.firstinspires.ftc.teamcode.vision.SkystoneRandomization
import kotlin.math.absoluteValue

@Config
abstract class DoubleSkystoneIntake(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL -8.625, Field.SOUTH_WALL +38.25, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var xOffset = 14.0
        @JvmField
        var yOffset = 30.0
        @JvmField
        var intakeAngleOffset = -50.0
        @JvmField
        var intakeSpeed = 0.5
    }

    val stoneOrder = ArrayList<QuarryLocation>()

    enum class progStages {
        goingToIntakeAngle,
        goForwardToIntake,
        stopDoNothing,
    }

    override fun onStart() {
        when (SkystoneDetector.place) {
            SkystoneRandomization.FAR -> {
                stoneOrder.add(QuarryLocation.FAR_LEFT)
                stoneOrder.add(QuarryLocation.NEAR_LEFT)
            }
            SkystoneRandomization.MID -> {
                stoneOrder.add(QuarryLocation.FAR_MIDDLE)
                stoneOrder.add(QuarryLocation.NEAR_MIDDLE)
            }
            SkystoneRandomization.NEAR -> {
                stoneOrder.add(QuarryLocation.FAR_RIGHT)
                stoneOrder.add(QuarryLocation.NEAR_RIGHT)
            }
        }
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        telemetry.addData("currentStage", currentStage)

        when (currentStage) {
            progStages.goingToIntakeAngle -> {
                if (changedStage)
                    lift.triggerIntake()
                val stone = Quarry[QuarryLocation.FAR_LEFT]
                val error = goToPosition_mirror(stone.center_x + xOffset, stone.center_y + yOffset, -90.0 + intakeAngleOffset)
                telemetry.addData("hypot", error.point.hypot)
                telemetry.addData("deg", error.deg)
                if (error.deg.absoluteValue < 2.0 && error.point.hypot < 3.0)
                    nextStage()
            }

            progStages.goForwardToIntake -> {
                intake.state = MainIntake.State.IN
                pointAngle_mirror(-90.0 + intakeAngleOffset)

                movement_y = intakeSpeed

                if (isTimedOut(3.0) || world_x_mirror < 20.0 || intake.sensorTriggered)
                    nextStage()
            }

            progStages.stopDoNothing -> {
                intake.state = MainIntake.State.STOP
                stopDrive()
            }
        }
    }
}

@Autonomous
class DoubleSkystoneIntake_RED : DoubleSkystoneIntake(Alliance.RED)