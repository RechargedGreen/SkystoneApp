package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.AutoClaw
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.autoClaw
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.PurePursuit
import org.firstinspires.ftc.teamcode.movement.PurePursuitPath
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import org.firstinspires.ftc.teamcode.vision.SkystoneRandomization
import kotlin.math.absoluteValue

private val startPoint = Point(Field.EAST_WALL - 8.625, Field.SOUTH_WALL + 38.25)

@Config
abstract class HybridAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(startPoint.x, startPoint.y, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var grabX = 32.5
        @JvmField
        var followDistance = 35.0

        @JvmField
        var crossX = 38.0

        @JvmField
        var toFoundationX = 44.0
    }

    private val stoneYs = arrayOf(-59.0, -51.0, -43.0, -35.0, -27.0, -19.0)
    private val nearStones = arrayOf(2, 5, 4, 3/*, 1*/)
    private val midStones = arrayOf(1, 4, 5, 3/*, 2*/)
    private val farStones = arrayOf(0, 3, 5, 4/*, 2*/)
    private val stones by lazy {

        when (SkystoneDetector.place) {
            SkystoneRandomization.NEAR -> nearStones
            SkystoneRandomization.MID -> midStones
            SkystoneRandomization.FAR -> farStones
        }
    }

    val stone get() = stones[cycle]
    private var cycle = 0

    private val grabY get() = stoneYs[stone]

    enum class progStages {

        goBack,
        goToStone,

        grab,

        cross,

        eject,

        rotate,
        backUp,
        pull,
        rotateFoundation,
        park,

        stopDoNothing
    }

    override fun onStart() {
        super.onStart()
        nextStage(progStages.goToStone.ordinal)
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        telemetry.addData("stage", currentStage)

        stopDrive()

        when (currentStage) {
            progStages.goBack -> {
                val path = PurePursuitPath(followDistance)
                path.add(Point(crossX, 72.0))
                path.toY(grabY)
                PurePursuit.followCurve(path, 180.0)

                pointAngle_mirror(0.0)

                if (world_y_mirror < 5.0)
                    nextStage()
            }

            progStages.goToStone -> {
                autoClaw.state = AutoClaw.State.PRE_GRAB
                val error = goToPosition_mirror(grabX, grabY, 0.0)
                if (error.point.y.absoluteValue < 2.5 && error.point.x.absoluteValue < 2.5)
                    nextStage()
            }

            progStages.grab -> {
                autoClaw.state = AutoClaw.State.GRABBING
                val error = goToPosition_mirror(grabX, grabY, 0.0)
                if (isTimedOut(0.5))
                    nextStage()
            }

            progStages.cross -> {
                autoClaw.state = AutoClaw.State.STOW_STONE
                val curve = PurePursuitPath(followDistance)
                curve.add(Point(toFoundationX, grabY))
                curve.toY(24.0)
                curve.add(Point(29.0, if (cycle < 2) 56.0 else 46.0))

                val doneWithCurve = PurePursuit.followCurve(curve)

                pointAngle_mirror(0.0)

                if (stone < 5 && cycle < 2 && !isTimedOut(0.25))
                    stopDrive()

                if (doneWithCurve) {
                    autoClaw.state = AutoClaw.State.PRE_GRAB
                    nextStage()
                }
            }

            progStages.eject -> {
                if (isTimedOut(0.25)) {
                    cycle++
                    if (cycle >= stones.size) {
                        autoClaw.state = AutoClaw.State.TELEOP
                        foundationGrabber.prepForGrab()
                        nextStage()
                    } else {
                        nextStage(progStages.goBack.ordinal)
                        autoClaw.state = AutoClaw.State.TELEOP
                    }
                }
            }

            progStages.rotate -> {
                if (isTimedOut(0.2)) {
                    val error = goToPosition_mirror(28.0, 49.5, 90.0)
                    if (error.deg.absoluteValue < 6.0 && error.x.absoluteValue < 3.0)
                        nextStage()
                }
            }

            progStages.backUp -> {
                val error = goToPosition_mirror(23.0, 49.5, 90.0)
                if (error.x.absoluteValue < 3.0)
                    nextStage()
            }

            progStages.pull -> {
                foundationGrabber.grab()
                if (isTimedOut(0.25)) {
                    val error = goToPosition_mirror(53.0, 49.5, 90.0)
                    if (error.x.absoluteValue < 3.0)
                        nextStage()
                }
            }

            progStages.rotateFoundation -> {
                val error = pointAngle_mirror(180.0)

                //movement_turn = Range.clip(movement_turn, -0.5, 0.5)

                if (error.absoluteValue < 5.0) {
                    nextStage()
                    foundationGrabber.release()
                }
            }

            progStages.park -> {
                val error = goToPosition_mirror(31.0, 4.5, 180.0)
                if (error.point.hypot < 3.0)
                    nextStage()
            }

            progStages.stopDoNothing -> {
                if (isTimedOut(1.0))
                    requestOpModeStop()
            }
        }
    }
}

@Autonomous
class HybridAuto_Red : HybridAuto(Alliance.RED)

@Autonomous
class HybridAuto_BLUE : HybridAuto(Alliance.BLUE)