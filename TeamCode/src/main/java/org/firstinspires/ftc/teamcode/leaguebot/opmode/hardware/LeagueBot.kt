package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.qualcomm.hardware.lynx.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.hMap
import org.firstinspires.ftc.teamcode.leaguebot.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import org.firstinspires.ftc.teamcode.sharedhardware.*

object LeagueBot : BaseBot {
    const val placeWidth = 18.0
    const val placeLength = 18.0

    lateinit var drive: Akira
    lateinit var lift: SuperSonicLift
    lateinit var intake: MainIntake

    lateinit var lynx1: LynxModule
    lateinit var lynx2: LynxModule

    lateinit var gyro: OptimizedGyro

    lateinit var foundationGrabber: LeagueFoundationGrabber

    private lateinit var odometryPuller: OdometryPuller

    lateinit var extension: Extension
    lateinit var grabber: Grabber

    override fun setup() {
        lynx1 = hMap.get(LynxModule::class.java, "Expansion Hub 1")
        lynx2 = hMap.get(LynxModule::class.java, "Expansion Hub 2")
        gyro = OptimizedGyro(lynx2, OptimizedGyro.Mounting.FLAT)
        drive = Akira(LeagueMovementConstants)

        lift = SuperSonicLift()
        intake = MainIntake()

        grabber = Grabber()
        extension = Extension()

        foundationGrabber = LeagueFoundationGrabber()

        //odometryPuller = OdometryPuller({0.0}, {1.0})

        RoadRunner.reset()

        LeagueMovementConstants.setup()

        ScorerState.update()
    }

    override fun update() {
        LeagueThreeWheelOdometry.updateThreeWheel()
        //LeagueThreeWheelOdometry.updateTwoWheel()
        drive.update()

        grabber.update()
        lift.updateManualTemp()
        intake.update()

        extension.update()

        RoadRunner.update()

        ScorerState.update()
        //foundationGrabber.update()
        //odometryPuller.update()
    }

    fun endDoNothing() {
        stopDrive()
        foundationGrabber.release()
    }

    override val teleopName: String = LeagueTeleOp::class.java.name
}