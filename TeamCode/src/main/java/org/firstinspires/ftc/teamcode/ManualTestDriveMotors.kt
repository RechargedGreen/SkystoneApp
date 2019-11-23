package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
@Disabled
class ManualTestDriveMotors : LinearOpMode(){
    override fun runOpMode() {
        val lf = hardwareMap.dcMotor.get("leftFront")
        val lb = hardwareMap.dcMotor.get("leftBack")
        val rf = hardwareMap.dcMotor.get("rightFront")
        val rb = hardwareMap.dcMotor.get("rightBack")
        while (!isStopRequested){
            lf.power = if(gamepad1.a) 1.0 else 0.0
            lb.power = if(gamepad1.b) 1.0 else 0.0
            rf.power = if(gamepad1.x) 1.0 else 0.0
            rb.power = if(gamepad1.y) 1.0 else 0.0
        }
    }
}