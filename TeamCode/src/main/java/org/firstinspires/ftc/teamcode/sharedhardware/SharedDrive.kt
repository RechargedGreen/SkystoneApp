package org.firstinspires.ftc.teamcode.sharedhardware

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.lib.hardware.*
import org.firstinspires.ftc.teamcode.movement.*

class SharedDrive {
    private val leftFront = RevHubMotor("leftFront", DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val leftBack = RevHubMotor("leftBack", DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val rightFront = RevHubMotor("rightFront", DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val rightBack = RevHubMotor("rightBack", DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)

    fun update() {
        val x = DriveMovement.movement_x
        val y = DriveMovement.movement_y
        val turn = DriveMovement.movement_turn
    }
}