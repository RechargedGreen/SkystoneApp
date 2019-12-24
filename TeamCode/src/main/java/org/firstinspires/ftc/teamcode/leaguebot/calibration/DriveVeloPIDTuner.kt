package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.drive
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance

@Config
@TeleOp(group = "c")
class DriveVeloPIDTuner : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {

    companion object {
        @JvmField
        var p = 0.0
        @JvmField
        var i = 0.0
        @JvmField
        var d = 0.0
        @JvmField
        var f = 0.0
    }

    val distance = 24.0 * 4.0

    var profile = MotionProfileGenerator.generateSimpleMotionProfile(
            MotionState(0.0, 0.0, 0.0),
            MotionState(distance, 0.0, 0.0),
            50.0,
            60.0
    )

    enum class progStages {
        forward,
        backward,
        waiting
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        for (motor in drive.motors)
            motor.veloPIDF = PIDFCoefficients(p, i, d, f)

        when (currentStage) {
            progStages.forward, progStages.backward -> {
                val time = stageTimer.seconds()

                val motionState = profile[time]

                val velocity = RoadRunnerConstraints.kV * motionState.v

                val desiredCPS = velocity * 28.0 * 19.2

                movement_y = velocity

                if (time > profile.duration()) {
                    profile = profile.flipped()
                    nextStage()
                }

                combinedPacket.put("desiredCPS", desiredCPS)

                val averageCPS = drive.motors.map { it.velocity }.average()
                combinedPacket.put("averageCPS", averageCPS)

                combinedPacket.put("cpsError", averageCPS - desiredCPS)

            }
            progStages.waiting -> {
                stopDrive()
                telemetry.addLine("press b to continue")
                if (driver.b.currentState)
                    nextStage(0)
            }
        }
    }
}