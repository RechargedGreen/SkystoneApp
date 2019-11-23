package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.setPosition_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import kotlin.math.absoluteValue

@TeleOp
@Disabled
class KStaticCalibration : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)){
    val rampUpTime = 20.0

    var newY = 0.0
    var newX = 0.0
    var newT = 0.0

    enum class progStates {
        rampY,
        stopY,
        rampX,
        stopX,
        rampT,
        stop
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)

        when(currentStage){
            progStates.rampY -> {
                if(changedStage) {
                    setPosition_mirror(0.0, 0.0, 0.0)
                }
                val velocity = stageTimer.seconds() / rampUpTime
                movement_y = velocity
                if(world_point_mirror.hypot.absoluteValue > 0.01){
                    newY = velocity
                    nextStage()
                }
            }
            progStates.stopY -> {
                stopDrive()
                timeoutStage(5.0)
            }
            progStates.rampX -> {
                if(changedStage) {
                    setPosition_mirror(0.0, 0.0, 0.0)
                }
                val velocity = stageTimer.seconds() / rampUpTime
                movement_x = velocity
                if(world_point_mirror.hypot.absoluteValue > 0.01){
                    newX = velocity
                    nextStage()
                }
            }
            progStates.stopX -> {
                stopDrive()
                timeoutStage(5.0)
            }
            progStates.rampT -> {
                if(changedStage) {
                    setPosition_mirror(0.0, 0.0, 0.0)
                }
                val velocity = stageTimer.seconds() / rampUpTime
                movement_turn = velocity
                if(world_angle_mirror.deg.absoluteValue > 0.1){
                    newT = velocity
                    nextStage()
                }
            }
            progStates.stop -> {
                stopDrive()
                telemetry.addData("y", newY)
                telemetry.addData("x", newX)
                telemetry.addData("t", newT)
            }
        }
    }
}