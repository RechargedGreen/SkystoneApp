package org.firstinspires.ftc.teamcode.roverBot.hardware

import com.qualcomm.hardware.bosch.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.lib.hardware.*

class RoverDrive {
    private val lf = RevHubMotor("lf", DcMotor.RunMode.RUN_WITHOUT_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val lb = RevHubMotor("lb", DcMotor.RunMode.RUN_WITHOUT_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val rf = RevHubMotor("rf", DcMotor.RunMode.RUN_WITHOUT_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)
    private val rb = RevHubMotor("rb", DcMotor.RunMode.RUN_WITHOUT_ENCODER, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE)

    fun applyMovement() {
        val left = RoverMovement.movement_forward + RoverMovement.movement_turn
        val right = RoverMovement.movement_forward - RoverMovement.movement_turn
        lf.power = left
        lb.power = left
        rf.power = right
        rb.power = right
    }
}

object RoverMovement {
    fun updateGyro() {
        val angles = gyro.angularOrientation
        zRaw = angles.firstAngle.toDouble()
        xRaw = angles.secondAngle.toDouble()
        yRaw = angles.thirdAngle.toDouble()
    }

    lateinit var gyro: BNO055IMU

    var movement_forward = 0.0
    var movement_turn = 0.0

    val angle_deg: Double
        get() = zRaw + zBias
    val x_deg: Double
        get() = xRaw + xBias
    val y_deg: Double
        get() = yRaw + yBias
    val tilt_deg: Double
        get() = x_deg + y_deg

    private var zRaw = 0.0
    private var xRaw = 0.0
    private var yRaw = 0.0

    private var xBias = 0.0
    private var yBias = 0.0
    private var zBias = 0.0

    fun setAngle(angle: Double) {
        zBias = angle - zRaw
    }

    fun calibrateTilt() {
        xBias = -xRaw
        yBias = -yRaw
    }

    fun stopMovement() = move(0.0, 0.0)

    fun move(forward: Double, turn: Double) {
        movement_forward = forward
        movement_turn = turn
    }

    fun setup() {
        movement_forward = 0.0
        movement_turn = 0.0
    }
}