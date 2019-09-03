package org.firstinspires.ftc.teamcode.leaguebot

import com.qualcomm.hardware.lynx.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.hMap
import org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.sharedhardware.*

object LeagueBot : BaseBot {
    lateinit var drive: Akira

    lateinit var lynx1: LynxModule
    lateinit var lynx2: LynxModule

    lateinit var gyro: OptimizedGyro

    override fun setup() {
        lynx2 = hMap.get(LynxModule::class.java, "Expansion Hub 2")
        gyro = OptimizedGyro(lynx2, OptimizedGyro.Mounting.FLAT)
        drive = Akira(LeagueMovementConstants)
    }

    override fun update() {
        /*LeagueThreeWheelOdometry.updateThreeWheel()*/
        LeagueThreeWheelOdometry.updateTwoWheel()
        drive.update()
    }

    override val teleopName: String = LeagueTeleOp::class.java.name
}