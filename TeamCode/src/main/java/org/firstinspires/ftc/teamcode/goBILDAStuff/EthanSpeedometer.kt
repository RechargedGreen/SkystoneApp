package org.firstinspires.ftc.teamcode.goBILDAStuff

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.util.Clock

class EthanSpeedometer : LinearOpMode() {
    val ticksPerUnit:Double = 1.0
    override fun runOpMode() {
        val reads = HashMap<Double, Double>()

        val lf = hardwareMap.dcMotor.get("lf").apply {

        }
        val lb = hardwareMap.dcMotor.get("lb").apply {
            direction = DcMotorSimple.Direction.REVERSE
        }
        val rf = hardwareMap.dcMotor.get("rf").apply {
            direction = DcMotorSimple.Direction.REVERSE
        }
        val rb = hardwareMap.dcMotor.get("rb").apply {
            direction = DcMotorSimple.Direction.REVERSE
        }

        val encoder = lf


        val startTicks = encoder.currentPosition
        val startTime = Clock.seconds
        while(!isStopRequested){
            val distanceTraveled = (encoder.currentPosition - startTicks) / ticksPerUnit
        }
    }
}