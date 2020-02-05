package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.hardware.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.PurePursuit
import org.firstinspires.ftc.teamcode.movement.PurePursuit.angleWrap_deg
import org.firstinspires.ftc.teamcode.movement.PurePursuitPath
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_deg_mirror
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

        intakingSecondStone,
        drivingBackSecondStone,
        droppingSecondStone,

        intakingThirdStone,
        drivingBackThirdStone,
        droppingThirdStone,


        stopDoNothing
    }

    override fun onStart() {
        intake.state = MainIntake.State.IN
        lift.triggerIntake()
        PurePursuit.reset()
        intakePaths.add {
            val firstIntakePath = PurePursuitPath(20.0)
            firstIntakePath.add(startPoint)
            firstIntakePath.extrude(20.0, -90.0 - nearAngle)
            firstIntakePath.moveSpeed = 0.4
            firstIntakePath.extrude(30.0, -90.0 - nearAngle)
            firstIntakePath
        }

        intakePaths.add {
            val secondIntakePath = PurePursuitPath(20.0)
            secondIntakePath.add(Point(0.0, 48.0))
            secondIntakePath.toX(60.0)

            secondIntakePath.toY(20.0)
            secondIntakePath.add(Point(36.0, -12.0))

            secondIntakePath.moveSpeed = 0.4
            secondIntakePath.forceMoveSpeedEarly = true

            secondIntakePath.add(Point(24.0, -24.0))

            secondIntakePath
        }

        intakePaths.add {
            val thirdIntakePath = PurePursuitPath(20.0)
            thirdIntakePath.add(Point(40.0, 48.0))
            thirdIntakePath.toY(-13.0)
            thirdIntakePath.extrude(40.0, -135.0)
            thirdIntakePath
        }

        outtakePaths.add {
            val firstOuttakePath = PurePursuitPath(20.0)
            firstOuttakePath.add(it)
            firstOuttakePath.toX(40.0)
            firstOuttakePath.toY(20.0)
            //firstOuttakePath.followDistance = 10.0
            firstOuttakePath.toY(48.0)
            firstOuttakePath.toX(0.0)
            firstOuttakePath
        }

        outtakePaths.add {
            val secondOuttakePath = PurePursuitPath(20.0)
            secondOuttakePath.finalAngle = 180.0
            secondOuttakePath.add(Point(0.0, -24.0))
            secondOuttakePath.toX(40.0)
            secondOuttakePath.toY(36.0)
            secondOuttakePath
        }

        outtakePaths.add {
            val thirdOuttakePath = PurePursuitPath(20.0)
            thirdOuttakePath.add(it)
            thirdOuttakePath.add(Point(40.0, -12.0))
            thirdOuttakePath.toY(12.0)
            thirdOuttakePath.add(Point(50.0, 24.0))
            thirdOuttakePath.toY(32.0)
            thirdOuttakePath
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
                val doneWithCurve = PurePursuit.followCurve(path)
                if (intake.sensorTriggered || doneWithCurve) {
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

                if (hasCrossedY && world_x_mirror < 26.0) {
                    nextStage()
                    foundationGrabber.grab()
                    hasCrossedY = false
                    ScorerState.triggerRelease()
                    PurePursuit.reset()
                }
            }

            progStages.intakingSecondStone -> {
                intake.state = MainIntake.State.IN

                var doneWithCurve = false
                if (isTimedOut(1.0))
                    doneWithCurve = PurePursuit.followCurve(intakePaths[1]())

                if (angleWrap_deg(world_deg_mirror) > 170.0 || angleWrap_deg(world_deg_mirror) < 0.0) {
                    foundationGrabber.release()
                    ScorerState.triggerPullBack()
                }

                if (world_y_mirror < -4.0)
                    lift.triggerIntake()

                if (intake.sensorTriggered || doneWithCurve) {
                    intake.state = MainIntake.State.FINISH_AUTO_INTAKE
                    nextStage()
                    PurePursuit.reset()
                }
            }

            progStages.drivingBackSecondStone -> {
                val doneWithCurve = PurePursuit.followCurve(outtakePaths[1](Point(0.0, 0.0)), 180.0)
                if (world_y_mirror > 8.0)
                    ScorerState.triggerExtend()
                if (doneWithCurve)
                    nextStage()
            }

            progStages.droppingSecondStone -> {
                moveFieldCentric_mirror(0.0, 0.2, 0.0)
                pointAngle_mirror(180.0)
                ScorerState.triggerRelease()
                if (isTimedOut(0.5)) {
                    ScorerState.triggerPullBack()
                    nextStage()
                    PurePursuit.reset()
                }
            }

            progStages.intakingThirdStone -> {
                intake.state = MainIntake.State.IN
                lift.triggerIntake()

                val doneWithCurve = PurePursuit.followCurve(intakePaths[2]())

                if (world_y_mirror < -4.0)
                    lift.triggerIntake()

                if (doneWithCurve || intake.sensorTriggered)
                    nextStage()
            }

            progStages.drivingBackThirdStone -> {
                val doneWithCurve = PurePursuit.followCurve(outtakePaths[2](Point(0.0, 0.0)), 180.0)
                if (world_y_mirror > 8.0)
                    ScorerState.triggerExtend()
                if (world_y_mirror > 18.0)
                    lift.heightTarget = 4.0
                if (doneWithCurve)
                    nextStage()
            }

            progStages.droppingThirdStone -> {
                moveFieldCentric_mirror(0.0, 0.2, 0.0)
                pointAngle_mirror(180.0)
                val backTime = 0.7
                if (isTimedOut(backTime))
                    ScorerState.triggerRelease()
                if (isTimedOut(backTime + 0.5)) {
                    ScorerState.triggerPullBack()
                    nextStage()
                    PurePursuit.reset()
                }
            }
        }
    }
}

@Autonomous
class FourStone_RED : FourStone(Alliance.RED)