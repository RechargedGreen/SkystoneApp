package org.firstinspires.ftc.teamcode.fordBot

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.bulkLib.BulkDataMaster
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.util.AutomaticTeleopInit
import org.firstinspires.ftc.teamcode.util.Clock

abstract class FordBot(private val alliance: Alliance, val startingPosition: Double, val isAuto: Boolean = true) : LinearOpMode() {
    final override fun runOpMode() {
        ALLIANCE = alliance

        instance = this

        drive = FordDrive()
        extension = FordExtension()
        flip = FordFlip()
        intake = FordIntake()

        packet = TelemetryPacket()

        waitForStart()

        if (isAuto)
            AutomaticTeleopInit.transitionOnStop(this, "FordTeleop")

        drive.setAngle(startingPosition * alliance.sign)

        if (!isStopRequested)
            run()
    }

    final override fun waitForStart() {
        loop { isStarted }
    }

    fun update() {
        drive.update()
        extension.update()
        flip.update()
        intake.update()
    }

    companion object {
        lateinit var packet: TelemetryPacket
        lateinit var instance: FordBot

        lateinit var drive: FordDrive
        lateinit var extension: FordExtension
        lateinit var flip: FordFlip
        lateinit var intake: FordIntake

        fun loop(isDone: () -> Boolean) {
            while (!instance.isStopRequested) {
                packet = TelemetryPacket()

                BulkDataMaster.clearAllCaches()
                val isDone = isDone()
                instance.telemetry.update()
                FtcDashboard.getInstance().sendTelemetryPacket(packet)

                if (instance.isAuto || instance.isStarted)
                    instance.update()
                if (isDone)
                    break
            }
        }

        fun delay(seconds: Double) {
            if (seconds.isNaN()) {
                loop { false }
                return
            }

            val start = Clock.seconds
            loop { Clock.seconds - start > seconds }
        }
    }

    abstract fun run()
}