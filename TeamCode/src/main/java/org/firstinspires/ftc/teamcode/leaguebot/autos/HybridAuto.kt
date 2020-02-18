package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.AutoClaw
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.autoClaw
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.PurePursuit
import org.firstinspires.ftc.teamcode.movement.PurePursuitPath
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import kotlin.math.absoluteValue

private val startPoint = Point(Field.EAST_WALL - 8.625, Field.SOUTH_WALL + 38.25)

@Config
abstract class HybridAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(startPoint.x, startPoint.y, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var x = 33.0
        @JvmField
        var y = -43.0
    }

    enum class progStages {
        goToStone,

        grab,

        cross,


        stopDoNothing
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        stopDrive()

        when (currentStage) {
            progStages.goToStone -> {
                autoClaw.state = AutoClaw.State.PRE_GRAB
                val error = goToPosition_mirror(x, y, 0.0)
                if (error.point.y.absoluteValue < 2.0 && error.point.x.absoluteValue < 2.0)
                    nextStage()
            }

            progStages.grab -> {
                autoClaw.state = AutoClaw.State.GRABBING
                val error = goToPosition_mirror(x, y, 0.0)
                if (isTimedOut(0.4))
                    nextStage()
            }

            progStages.cross -> {
                autoClaw.state = AutoClaw.State.STOW_STONE
                val curve = PurePursuitPath(20.0)
                curve.add(Point(40.0, y))
                curve.toY(24.0)
                curve.add(Point(30.0, 48.0))

                val doneWithCurve = PurePursuit.followCurve(curve)

                pointAngle_mirror(0.0)

                if (doneWithCurve) {
                    autoClaw.state = AutoClaw.State.PRE_GRAB
                    nextStage()
                }
            }

            progStages.stopDoNothing -> {
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }
        }
    }
}

@Autonomous
class HybridAuto_Red : HybridAuto(Alliance.RED)