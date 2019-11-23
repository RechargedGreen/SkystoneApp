package org.firstinspires.ftc.teamcode.visionTesting

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.ryanVision.*
import org.opencv.core.*

@TeleOp(group = "b")
class BasicBeaconDetectionTest : LinearOpMode() {
    override fun runOpMode() {
        val camera = VuforiaCamera()
        val beacon = BasicBeaconDetectionTracker()
        camera.addTracker(beacon)
        camera.initialize()
        while (!isStopRequested) {
            telemetry.addData("color", beacon.lastColor)
            telemetry.update()
        }
    }
}

class BasicBeaconDetectionTracker : TrackerShortcut() {
    enum class Color {
        RED,
        BLUE
    }

    var lastColor = Color.RED
    override fun processFrame(frame: Mat, timestamp: Double) {
        val total = Core.sumElems(frame).`val`
        lastColor = if (total[0] > total[2]) Color.RED else Color.BLUE
    }
}