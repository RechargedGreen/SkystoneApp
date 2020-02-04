package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.grabber
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.hardware.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.PurePursuit
import org.firstinspires.ftc.teamcode.movement.PurePursuitPath
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_point_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance

val startPoint = Point(Field.EAST_WALL - 8.625, Field.SOUTH_WALL + 38.25)

@Config
abstract class FourStone(val alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(startPoint.x, startPoint.y, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var nearAngle = 25.0
    }

    val intakePaths = ArrayList<() -> PurePursuitPath>()
    val outtakePaths = ArrayList<(Point) -> PurePursuitPath>()

    lateinit var outPath: PurePursuitPath

    var hasCrossedY = false

    enum class progStages {
        intakingFirstStone,
        outTakingFirstStone,
        stopDoNothing
    }

    override fun onStart() {
        intake.state = MainIntake.State.IN
        lift.triggerIntake()
        PurePursuit.reset()
        intakePaths.add {
            val firstIntakePath = PurePursuitPath(15.0)
            firstIntakePath.add(startPoint)
            firstIntakePath.extrude(20.0, -90.0 - nearAngle)
            firstIntakePath.moveSpeed = 0.4
            firstIntakePath.extrude(30.0, -90.0 - nearAngle)
            firstIntakePath
        }

        outtakePaths.add {
            val firstOuttakePath = PurePursuitPath(15.0)
            firstOuttakePath.add(it)
            firstOuttakePath.toX(40.0)
            firstOuttakePath.toY(20.0)
            firstOuttakePath.followDistance = 10.0
            firstOuttakePath.toY(48.0)
            firstOuttakePath.toX(0.0)
            firstOuttakePath
        }
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]
        telemetry.addData("currentStage", currentStage)

        stopDrive()
        when (currentStage) {
            progStages.stopDoNothing -> {
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }

            progStages.intakingFirstStone -> {
                val path = intakePaths[0]()
                PurePursuit.followCurve(path)
                if (intake.sensorTriggered) {
                    intake.state = MainIntake.State.FINISH_AUTO_INTAKE
                    nextStage()
                    outPath = outtakePaths[0](world_point_mirror)
                    PurePursuit.reset()
                }
            }

            progStages.outTakingFirstStone -> {
                PurePursuit.followCurve(outPath, 180.0)
                if (world_y_mirror > 10.0) {
                    hasCrossedY = true
                    foundationGrabber.prepForGrab()
                    ScorerState.triggerExtend()
                }

                if (hasCrossedY && world_x_mirror < 30.0) {
                    nextStage()
                    foundationGrabber.grab()
                    hasCrossedY = false
                    ScorerState.triggerRelease()
                }
            }
        }
    }
}

@Autonomous
class FourStone_RED : FourStone(Alliance.RED)