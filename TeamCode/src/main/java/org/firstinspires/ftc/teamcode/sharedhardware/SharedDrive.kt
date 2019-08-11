package org.firstinspires.ftc.teamcode.sharedhardware

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.hardware.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

class SharedDrive(provider:MovementConstantsProvider) {
    private val motorMode: DcMotor.RunMode
        get() = if (Globals.mode.isAutonomous) DcMotor.RunMode.RUN_USING_ENCODER else DcMotor.RunMode.RUN_WITHOUT_ENCODER

    private val leftFront = RevHubMotor("leftFront", Go_19_2::class).BRAKE().FORWARD().OPEN_LOOP()
    private val leftBack = RevHubMotor("leftBack", Go_19_2::class).BRAKE().FORWARD().OPEN_LOOP()
    private val rightFront = RevHubMotor("rightFront", Go_19_2::class).BRAKE().REVERSE().OPEN_LOOP()
    private val rightBack = RevHubMotor("rightBack", Go_19_2::class).BRAKE().REVERSE().OPEN_LOOP()

    fun update() {
        val x = DriveMovement.movement_x
        val y = DriveMovement.movement_y
        val turn = DriveMovement.movement_turn
    }
}