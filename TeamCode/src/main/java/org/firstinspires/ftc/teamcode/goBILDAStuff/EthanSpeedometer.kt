package org.firstinspires.ftc.teamcode.goBILDAStuff

import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.*

@TeleOp(group = "b")
class EthanSpeedometer : LinearOpMode() {
    val distanceScaler = 1.0 // if there is an imperfection in your wheel radius or anything, this can fine tune it

    val wheelDiameter = 1.0 // set these to your dead wheel and encoder
    val ticksPerRotation = 1.0

    val maximumTravelDistance = 1.0 // distance robot stops at

    val minimumWaitTime = 0.0 // minimum amount of seconds between loops. If this is zero it will likely run at about 333Hz, because of only one encoder read.

    val ticksPerUnit: Double = 1.0
    override fun runOpMode() {
        val reads = HashMap<Double, Double>()

        val lf = hardwareMap.dcMotor.get("lf").apply {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
        val lb = hardwareMap.dcMotor.get("lb").apply {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            direction = DcMotorSimple.Direction.REVERSE
        }
        val rf = hardwareMap.dcMotor.get("rf").apply {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            direction = DcMotorSimple.Direction.REVERSE
        }
        val rb = hardwareMap.dcMotor.get("rb").apply {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            direction = DcMotorSimple.Direction.REVERSE
        }

        val encoder = lf // you'll want to put this as the motor port with the dead wheel

        waitForStart()

        val timer = ElapsedTime()
        val loopTimer = ElapsedTime()

        lf.power = 1.0
        lb.power = 1.0
        rf.power = 1.0
        rb.power = 1.0

        mainLoop@ while (!isStopRequested) {
            val ticks = listOf(lf, lb, rf, rb).map { it.currentPosition }.average()
            val rotations = ticks / ticksPerRotation
            val distanceTraveled = rotations * Math.PI * wheelDiameter * distanceScaler

            reads[timer.seconds()] = distanceTraveled

            do {
                if (distanceTraveled >= maximumTravelDistance)
                    break@mainLoop
            } while (!isStopRequested && loopTimer.seconds() < minimumWaitTime)
            loopTimer.reset()
        }

        lf.power = 0.0
        lb.power = 0.0
        rf.power = 0.0
        rb.power = 0.0

        while (!isStopRequested) {
            for ((t, d) in reads)
                telemetry.addData(t.toString(), d)
            telemetry.update()
        }
    }
}