package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator
import com.acmerobotics.roadrunner.profile.MotionState
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.util.Clock
import org.firstinspires.ftc.teamcode.util.MathUtil
import java.util.*
import kotlin.math.absoluteValue

@Config
class mp1dprotoype : LinearOpMode() {
    lateinit var lf: DcMotor
    lateinit var lb: DcMotor
    lateinit var rf: DcMotor
    lateinit var rb: DcMotor

    var ticksPerInch = 1.0
    var kV = 1.0

    @JvmField
    var maxAccel = 40.0
    @JvmField
    var maxVel = 48.0
    @JvmField
    var drivePID = PIDCoefficients()
    var turnPID = PIDCoefficients()

    override fun runOpMode() {
        lf = hardwareMap.dcMotor.get("lf")
        lb = hardwareMap.dcMotor.get("lb")
        rf = hardwareMap.dcMotor.get("rf")
        rb = hardwareMap.dcMotor.get("rb")
        waitForStart()
    }

    fun turn(degrees: Double) {
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

        while (!isStopRequested && Clock.seconds - startTime <= profile.duration()) {
            val currentMotionState = profile[Clock.seconds - startTime]
            feedbackController.targetPosition = currentMotionState.x
            val currentPosition = arrayOf(lf.currentPosition, lb.currentPosition, rf.currentPosition, rb.currentPosition).average() / ticksPerInch
            val forwardSpeed = feedbackController.update(currentPosition, currentMotionState.v, currentMotionState.a)
            val turnSpeed = turnController.update(MathUtil.angleWrap(getAngle() - degrees))
            setVelocity(forwardSpeed, turnSpeed)
        }
        setVelocity(0.0, 0.0)
    }

    fun getAngle() = 0.0

    fun setVelocity(forward: Double, turn: Double) {
        val left = forward + turn
        val right = forward - turn
        val max = Collections.max(arrayListOf(1.0, left.absoluteValue, right.absoluteValue))
        lf.power = left.absoluteValue
        lb.power = left.absoluteValue
        rf.power = left.absoluteValue
        rb.power = left.absoluteValue
    }
}