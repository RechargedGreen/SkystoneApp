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
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_deg_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_point_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import org.firstinspires.ftc.teamcode.vision.SkystoneRandomization

val startPoint = Point(Field.EAST_WALL - 8.625, Field.SOUTH_WALL + 38.25)

@Config
abstract class FourStone(val alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(startPoint.x, startPoint.y, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var nearAngle = 25.0
        @JvmField
        var midAngle = 30.0
        @JvmField
        var farAngle = 39.0
        @JvmField
        var intakeMoveSpeed = 0.4
    }

    val intakePaths = ArrayList<(Point) -> PurePursuitPath>()
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

        intakingFourthStone,
        drivingBackFourthStone,
        droppingFourthStone,

        yeetToPark,

        stopDoNothing
    }

    fun setupIntakePaths() {
        when (SkystoneDetector.place) {
            SkystoneRandomization.NEAR -> setupNearIntakePaths()
            SkystoneRandomization.MID -> setupMidIntakePaths()
            SkystoneRandomization.FAR -> setupFarIntakePaths()
        }
    }

    fun setupOuttakePaths() {
        /*outtakePaths.add {
            val firstOuttakePath = PurePursuitPath(20.0)
            firstOuttakePath.add(it)
            firstOuttakePath.toX(38.0)
            firstOuttakePath.toY(20.0)
            firstOuttakePath.toY(44.0)
            firstOuttakePath.toX(0.0)
            firstOuttakePath
        }*/

        outtakePaths.add {
            val firstOuttakePath = PurePursuitPath(20.0)
            firstOuttakePath.add(it)
            firstOuttakePath.toX(38.0)
            firstOuttakePath.toY(0.0)
            firstOuttakePath.add(Point(56.0, 30.0))
            firstOuttakePath.toY(44.0)
            firstOuttakePath.toX(0.0)
            firstOuttakePath
        }

        outtakePaths.add {
            val secondOuttakePath = PurePursuitPath(20.0)
            secondOuttakePath.finalAngle = 180.0
            secondOuttakePath.add(it)
            secondOuttakePath.toX(38.0)
            secondOuttakePath.toY(36.0)
            secondOuttakePath
        }

        outtakePaths.add {
            val thirdOuttakePath = PurePursuitPath(20.0)
            thirdOuttakePath.add(it)
            thirdOuttakePath.toX(38.0)
            thirdOuttakePath.toY(12.0)
            thirdOuttakePath.add(Point(50.0, 24.0))
            thirdOuttakePath.toY(32.0)
            thirdOuttakePath
        }

        outtakePaths.add {
            val fourthOuttakePath = PurePursuitPath(20.0)
            fourthOuttakePath.add(it)
            fourthOuttakePath.toX(38.0)
            fourthOuttakePath.toY(18.0)
            fourthOuttakePath.add(Point(45.0, 30.0))
            fourthOuttakePath.toY(32.0)
            fourthOuttakePath
        }
    }

    fun setupNearIntakePaths() {
        intakePaths.add {
            val firstIntakePath = PurePursuitPath(20.0)
            firstIntakePath.add(startPoint)
            firstIntakePath.moveSpeed = intakeMoveSpeed
            firstIntakePath.extrude(53.0, -90.0 - nearAngle)
            firstIntakePath
        }

        intakePaths.add {
            val secondIntakePath = PurePursuitPath(20.0)
            secondIntakePath.add(Point(0.0, 44.0))
            secondIntakePath.toX(58.0)

            secondIntakePath.toY(30.0) // was 20
            secondIntakePath.moveSpeed = intakeMoveSpeed
            secondIntakePath.add(Point(36.0, -12.0))

            secondIntakePath.moveSpeed = intakeMoveSpeed
            secondIntakePath.add(Point(24.0, -24.0))
            secondIntakePath.extend(5.0)

            secondIntakePath
        }

        intakePaths.add {
            val thirdIntakePath = PurePursuitPath(20.0)
            thirdIntakePath.add(Point(38.0, 48.0))
            thirdIntakePath.toY(-13.0)
            thirdIntakePath.forceMoveSpeedEarly = true
            thirdIntakePath.moveSpeed = intakeMoveSpeed
            thirdIntakePath.extrude(50.0, -135.0)
            thirdIntakePath
        }

        intakePaths.add {
            val fourthIntakePath = PurePursuitPath(20.0)

            fourthIntakePath.add(it)
            fourthIntakePath.add(Point(38.0, 12.0))
            fourthIntakePath.toY(-24.0)
            fourthIntakePath.add(Point(24.0, -64.0))

            fourthIntakePath
        }
    }

    fun setupMidIntakePaths() {
        intakePaths.add {
            val firstIntakePath = PurePursuitPath(20.0)
            firstIntakePath.add(startPoint)
            firstIntakePath.moveSpeed = intakeMoveSpeed
            firstIntakePath.extrude(60.0, -90.0 - midAngle)
            firstIntakePath
        }

        intakePaths.add {
            val secondIntakePath = PurePursuitPath(20.0)
            secondIntakePath.add(Point(0.0, 44.0))
            secondIntakePath.toX(53.0)

            secondIntakePath.toY(30.0) // was 20
            secondIntakePath.add(Point(38.0, 0.0))

            secondIntakePath.toY(-10.0)

            secondIntakePath.moveSpeed = intakeMoveSpeed
            secondIntakePath.forceMoveSpeedEarly = true

            secondIntakePath.toY(-22.0)

            secondIntakePath.extrude(15.0, -140.0)

            secondIntakePath
        }
    }

    fun setupFarIntakePaths() {
        intakePaths.add {
            val firstIntakePath = PurePursuitPath(20.0)
            firstIntakePath.add(startPoint)
            firstIntakePath.moveSpeed = intakeMoveSpeed
            firstIntakePath.extrude(60.0, -90.0 - farAngle)
            firstIntakePath
        }
    }

    override fun onInitLoop() {
        telemetry.addData("order", SkystoneDetector.place)
    }

    override fun onStart() {
        intake.state = MainIntake.State.IN
        lift.triggerIntake()
        PurePursuit.reset()

        setupIntakePaths()
        setupOuttakePaths()
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
                val path = intakePaths[0](world_point_mirror)
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
                if (world_y_mirror > 10.0)
                    hasCrossedY = true

                if (hasCrossedY && world_deg_mirror > 0.0 && world_deg_mirror < 90.0 + 30.0) {
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
                    doneWithCurve = PurePursuit.followCurve(intakePaths[1](world_point_mirror))

                if (angleWrap_deg(world_deg_mirror) > 170.0 || angleWrap_deg(world_deg_mirror) < 0.0) {
                    foundationGrabber.release()
                    ScorerState.triggerPullBack()
                }

                if (world_y_mirror < 0.0)
                    lift.triggerIntake()

                if (intake.sensorTriggered || doneWithCurve) {
                    intake.state = MainIntake.State.FINISH_AUTO_INTAKE
                    nextStage()
                    PurePursuit.reset()

                    outPath = outtakePaths[1](world_point_mirror)
                }
            }

            progStages.drivingBackSecondStone -> {
                val doneWithCurve = PurePursuit.followCurve(outPath, 180.0)
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

                val doneWithCurve = PurePursuit.followCurve(intakePaths[2](world_point_mirror))

                if (world_y_mirror < -0.0)
                    lift.triggerIntake()

                if (doneWithCurve || intake.sensorTriggered) {
                    nextStage()
                    outPath = outtakePaths[2](world_point_mirror)
                }
            }

            progStages.drivingBackThirdStone -> {
                val doneWithCurve = PurePursuit.followCurve(outPath, 180.0)
                if (world_y_mirror > 8.0)
                    ScorerState.triggerExtend()
                if (doneWithCurve)
                    nextStage()
            }

            progStages.droppingThirdStone -> {
                moveFieldCentric_mirror(0.0, 0.2, 0.0)
                pointAngle_mirror(180.0)
                val backTime = 0.5
                if (isTimedOut(backTime))
                    ScorerState.triggerRelease()
                if (isTimedOut(backTime + 0.5)) {
                    ScorerState.triggerPullBack()
                    nextStage()
                    PurePursuit.reset()

                    outPath = intakePaths[3](world_point_mirror)
                }
            }

            progStages.intakingFourthStone -> {
                intake.state = MainIntake.State.IN
                lift.triggerIntake()

                val doneWithCurve = PurePursuit.followCurve(outPath)

                if (world_y_mirror < -0.0)
                    lift.triggerIntake()

                if (doneWithCurve || intake.sensorTriggered) {
                    nextStage()
                    outPath = outtakePaths[3](world_point_mirror)
                }
            }

            progStages.drivingBackFourthStone -> {
                val doneWithCurve = PurePursuit.followCurve(outPath, 180.0)
                if (world_y_mirror > 8.0)
                    ScorerState.triggerExtend()
                if (doneWithCurve)
                    nextStage()
            }

            progStages.droppingFourthStone -> {
                moveFieldCentric_mirror(0.0, 0.2, 0.0)
                pointAngle_mirror(180.0)
                val backTime = 0.5
                if (isTimedOut(backTime))
                    ScorerState.triggerRelease()
                if (isTimedOut(backTime + 0.5)) {
                    ScorerState.triggerPullBack()
                    nextStage()
                    PurePursuit.reset()
                }
            }

            progStages.yeetToPark -> {
                val error = goToPosition_mirror(36.0, 3.0, 180.0)
                if (error.point.hypot < 3.0 && Speedometer.fieldSpeed.hypot < 5.0) {
                    nextStage()
                    ScorerState.triggerLoad()
                }
            }
        }
    }
}

@Autonomous
class FourStone_RED : FourStone(Alliance.RED)

@Autonomous
class FourStone_BLUE : FourStone(Alliance.BLUE)