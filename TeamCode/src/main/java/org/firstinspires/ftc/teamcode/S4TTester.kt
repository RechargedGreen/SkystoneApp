package org.firstinspires.ftc.teamcode

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.bulkLib.*

@TeleOp(group = "b")
@Disabled
class S4TTester : LinearOpMode() {
    override fun runOpMode() {
        val module = hardwareMap.get(LynxModule::class.java, "lynx")
        var encoder = Encoder(module, 0, S4T.CPR_1000)
        var ticksPerRotation = 1000.0

        while (!isStopRequested) {
            BulkDataMaster.clearAllCaches()
            val ticks = encoder.ticks
            val rotations = encoder.rotations
            val radians = encoder.radians
            telemetry.addData("ticks", ticks)
            telemetry.addData("rotations", rotations)
            telemetry.addData("radians", radians)
            telemetry.update()
        }
    }
}