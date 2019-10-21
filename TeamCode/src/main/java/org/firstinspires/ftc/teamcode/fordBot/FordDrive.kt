package org.firstinspires.ftc.teamcode.fordBot

import RevHubMotor
import com.acmerobotics.roadrunner.control.*
import com.acmerobotics.roadrunner.profile.*
import com.qualcomm.robotcore.hardware.configuration.typecontainers.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.lib.hardware.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

class FordDrive {

    fun setAngle(deg: Double) {
        imu.heading_deg = deg
    }

    var allianceSign = 1.0

    private val lf = RevHubMotor("lf", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl
    private val lb = RevHubMotor("lb", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl
    private val rf = RevHubMotor("rf", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl
    private val rb = RevHubMotor("rb", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl

    private val motors = arrayOf(lf, lb, rf, rb)

    private val imu = OptimizedGyro(OptimizedGyro.Mounting.VERTICAL, FordBot.instance.hardwareMap)

    var movement_y = 0.0
    var movement_turn = 0.0

    private var degPerSecond = 0.0

    val deg get() = imu.heading_deg

    var lastDeg = Double.NaN
    var lastTime = Double.NaN

    fun update() {
        val left = movement_y + movement_turn
        val right = movement_turn - movement_turn

        val time = Clock.seconds
        val deg = imu.heading_deg
        if (!lastTime.isNaN())
            lastDeg = (deg - lastDeg) / (time - lastTime)
        lastDeg = deg
        lastTime = time

        lf.power = left
        lb.power = right
    }

    val y_pos = ticks.average() / ticksPerRev * C

    val ticks get() = motors.map { it.encoderTicks }

    fun driveInches(inches: Double, angle: Double) {
        val controller = PIDFController(axial_pid, kV)
        val profile = MotionProfileGenerator.generateSimpleMotionProfile(MotionState(y_pos, 0.0), MotionState(y_pos + inches, 0.0), maxVel, maxAccel)
        val startTime = Clock.seconds
        FordBot.loop {
            val time = Clock.seconds - startTime
            if (time > profile.duration())
                return@loop true
            val motionState = profile[time]
            controller.targetPosition = motionState.x
            movement_y = controller.update(y_pos, motionState.v, motionState.a)

            val degErrorLeft = pointAngle(angle)

            FordBot.packet.put("deg_errorLeft", degErrorLeft)

            val yErrorLeft = motionState.x - y_pos

            FordBot.packet.put("y_error", yErrorLeft)

            return@loop false
        }
        stop()
    }

    fun stop() {
        movement_turn = 0.0
        movement_y = 0.0
    }

    fun turnTo(deg: Double) {
        FordBot.loop { pointAngle(deg).absoluteValue < 2.0 }
        stop()
    }

    fun pointAngle(degT: Double): Double {
        val target = allianceSign * deg
        val degLeft = Angle.createWrappedDeg(target - deg).deg
        movement_turn = degLeft * turnP - degPerSecond * turnD
        return degLeft
    }

    companion object {
        @JvmField
        var maxVel = 40.0
        @JvmField
        var maxAccel = 40.0
        @JvmField
        var axial_pid = PIDCoefficients()

        @JvmField
        var turnP = 0.0

        @JvmField
        var turnD = 0.0


        private val motorType = MotorConfigurationType.getMotorType(ActualRev20::class.java)
        private const val C = Math.PI * 4.0
        private val ticksPerRev = motorType.ticksPerRev
        private val maxRPM = motorType.maxRPM
        private val kV = 1.0 / (maxRPM / 60.0 * C)
    }
}