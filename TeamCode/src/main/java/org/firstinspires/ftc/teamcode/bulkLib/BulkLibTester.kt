package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.lib.hardware.*

@TeleOp
class BulkLibTester : LinearOpMode() {
    override fun runOpMode() {
        val hub1 = hardwareMap.get(LynxModule::class.java, "hub1")
        val hub2 = hardwareMap.get(LynxModule::class.java, "hub2")

        val motor1 = RevHubMotor("motor1", Go_19_2::class)
        val motor2 = RevHubMotor("motor2")
        val motor3: DcMotorEx = RevHubMotor(hardwareMap.dcMotor.get("motor3"))

        val encoder1 = Encoder(hub1, 0, S4T.CPR_1000)
        val encoder2 = Encoder(hub1, 0, S4T.CPR_1000)
        val encoder3 = Encoder(hub1, 0, S4T.CPR_1000)

        val touch = RevHubTouchSensor("touch")
        val pot = RevHubPot("pot")

        val gyro = OptimizedGyro(OptimizedGyro.Mounting.VERTICAL)

        waitForStart()

        while (opModeIsActive()) {
            BulkDataMaster.clearAllCaches()

            telemetry.addData("m1", motor1.currentPosition)
            telemetry.addData("ticks", encoder1.ticks)
            telemetry.addData("rotations", encoder1.rotations)
            telemetry.addData("radians", encoder1.radians)
            telemetry.addData("pressed", touch.isPressed)
            telemetry.addData("pot degrees", pot.degrees)
            telemetry.addData("gyro degrees", gyro.heading_deg)
            telemetry.update()
            val v = hub1.cachedInput.getAnalogInput(0) / 1000.0
        }
    }
}