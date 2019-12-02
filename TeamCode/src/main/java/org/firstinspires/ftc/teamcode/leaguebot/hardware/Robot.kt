package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.qualcomm.hardware.lynx.LynxModule
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.hMap
import org.firstinspires.ftc.teamcode.bulkLib.OptimizedGyro
import org.firstinspires.ftc.teamcode.leaguebot.teleop.LeagueTeleOp
import org.firstinspires.ftc.teamcode.opmodeLib.BaseBot
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive

object Robot : BaseBot {
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

    lateinit var cap: Capstone

    override fun setup() {
        lynx1 = hMap.get(LynxModule::class.java, "Expansion Hub 1")
        lynx2 = hMap.get(LynxModule::class.java, "Expansion Hub 2")
        gyro = OptimizedGyro(lynx2)
        drive = Akira()

        lift = SuperSonicLift()
        intake = MainIntake()

        grabber = Grabber()
        extension = Extension()

        foundationGrabber = LeagueFoundationGrabber()

        odometryPuller = OdometryPuller()

        cap = Capstone()

        ScorerState.triggerLoad()
    }

    override fun update() {
//        RobotOdometry.updateThreeWheel()
        if (mode.isAutonomous)
            RobotOdometry.updateTwoWheel()
        drive.update()

        grabber.update()
        lift.update()
        intake.update()

        extension.update()

        foundationGrabber.update()

        ScorerState.update()
        odometryPuller.update()
        cap.update()
    }

    fun endDoNothing() {
        stopDrive()
        foundationGrabber.release()
    }

    override val teleopName: String = LeagueTeleOp::class.java.name
}