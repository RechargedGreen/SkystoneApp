package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.*
import com.acmerobotics.roadrunner.control.*
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.profile.*
import com.qualcomm.hardware.bosch.*
import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.util.*
import java.util.*
import kotlin.math.*

@Config
class mp1dprotoype : LinearOpMode() {
    lateinit var lf: DcMotor
    lateinit var lb: DcMotor
    lateinit var rf: DcMotor
    lateinit var rb: DcMotor

    lateinit var imu: BNO055IMU

    val driveWheelRadius = 2.0 // might need a small adjustment
    val wheelCircumfrence = driveWheelRadius * Math.PI * 2.0

    val motorRPM = 300.0 // in reality this rpm is slightly off but shouldn't matter
    val motorTicksPerRev = 28.0 * 19.2 // motors aren't actually 20:1, they are slightly faster, but this messes with the sdk stuff

    var ticksPerInch = motorTicksPerRev / wheelCircumfrence
    var kV = 1.0 / (motorRPM / 60.0 * wheelCircumfrence) // converts to 1.0/maxInchesPerSecond

    companion object {
        // tune this to close to your bot's limits but allow feedback room
        @JvmField
        var maxAccel = 40.0 // high is closer to slipping - low accelerates slower
        @JvmField
        var maxVel = 48.0 // you can tune this to something even faster, but this should be good

        // constants for feedback, minimizes any unpredictable error
        @JvmField
        var drivePID = PIDCoefficients()
        @JvmField
        var turnPID = PIDCoefficients()

    }

    override fun runOpMode() {
        lf = hardwareMap.dcMotor.get("lf")
        lb = hardwareMap.dcMotor.get("lb")
        rf = hardwareMap.dcMotor.get("rf")
        rb = hardwareMap.dcMotor.get("rb")
        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        val parameters = BNO055IMU.Parameters()
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES
        imu.initialize(parameters)

        waitForStart()

        drive(24.0, 0.0)
        turn(90.0)
        drive(24.0, 90.0)
        drive(-24.0, 90.0)
    }

    fun turn(degrees: Double) {
        val turnController = PIDFController(turnPID, 0.0, 0.0, 0.0, { 0.0 })
        whileLoop {
            val error = angleError(degrees)
            if (error.absoluteValue < 2.0)
                false
            setVelocity(0.0, turnController.update(error))
            true
        }
        setVelocity(0.0, 0.0)
    }

    fun drive(inches: Double, degrees: Double) {
        val profile = MotionProfileGenerator.generateSimpleMotionProfile(
                MotionState(0.0, 0.0),
                MotionState(inches, 0.0),
                maxVel,
                maxAccel
        )
        val startTime = Clock.seconds
        val feedbackController = PIDFController(drivePID, kV, 0.0, 0.0, { 0.0 })

        val turnController = PIDFController(turnPID, 0.0, 0.0, 0.0, { 0.0 })

        val startPosition = position()

        whileLoop {
            val time = Clock.seconds - startTime
            if (time > profile.duration())
                false

            val currentMotionState = profile[time]
            feedbackController.targetPosition = currentMotionState.x

            val currentPosition = position() - startPosition

            val forwardSpeed = feedbackController.update(currentPosition, currentMotionState.v, currentMotionState.a)
            val turnSpeed = 0.0//turnController.update(MathUtil.angleWrap(getAngle() - degrees))

            setVelocity(forwardSpeed, turnSpeed)

            true
        }
        setVelocity(0.0, 0.0)
    }

    fun position() = arrayOf(lf.currentPosition, lb.currentPosition, rf.currentPosition, rb.currentPosition).average() / ticksPerInch

    fun getAngle() = imu.angularOrientation.firstAngle

    fun setVelocity(forward: Double, turn: Double) {
        val left = forward + turn
        val right = forward - turn
        val max = Collections.max(arrayListOf(1.0, left.absoluteValue, right.absoluteValue))
        lf.power = left / max
        lb.power = left / max
        rf.power = right / max
        rb.power = right / max
    }

    fun whileLoop(condition: () -> Boolean) {
        while (!isStopRequested && condition());
    }

    fun angleError(target: Double) = angleWrap(getAngle() - target)

    fun angleWrap(degrees: Double): Double {
        var degrees = degrees
        while (degrees < -180.0)
            degrees += 360.0
        while (degrees > 180.0)
            degrees -= 360.0

        return degrees
    }
}