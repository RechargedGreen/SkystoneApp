package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.drive
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.gamepadControl
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
        var f = 11.8
    }

    val distance = 24.0 * 6.0

    var profile = MotionProfileGenerator.generateSimpleMotionProfile(
            MotionState(0.0, 0.0, 0.0),
            MotionState(distance, 0.0, 0.0),
            50.0,
            60.0
    )

    enum class progStages {
        forward,
        waiting1,
        backward,
        waiting2
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        for (motor in drive.motors)
            motor.veloPIDF = PIDFCoefficients(p, i, d, f)

        stopDrive()

        when (currentStage) {
            progStages.forward, progStages.backward -> {
                val time = stageTimer.seconds()

                val motionState = profile[time]

                val velocity = motionState.v

                movement_y = RoadRunnerConstraints.kV * velocity

                if (time > profile.duration()) {
                    profile = profile.flipped()
                    nextStage()
                }

                combinedPacket.put("desiredVel", velocity)

                val velocities = drive.wheelVelocities
                for (i in 0 until velocities.size)
                    combinedPacket.put("velocity_$i", velocities[i])

                combinedPacket.put("error", velocities.average() - velocity)

            }
            progStages.waiting1, progStages.waiting2 -> {
                gamepadControl(driver)
                telemetry.addLine("press b to continue")
                if (driver.b.currentState) {
                    nextStage()
                    if(stage >= progStages.values().size)
                        nextStage(0)
                }

                combinedPacket.put("desiredVel", 0.0)
                for (i in 0 until drive.motors.size)
                    combinedPacket.put("velocity_$i", 0)

                combinedPacket.put("error", 0.0)
            }
        }
    }
}