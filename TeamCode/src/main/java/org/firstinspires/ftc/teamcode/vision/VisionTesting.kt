package org.firstinspires.ftc.teamcode.vision

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.ryanVision.*

@TeleOp(group = "b")
class SkystoneVisionTest : LinearOpMode() {
    override fun runOpMode() {
        ALLIANCE = Alliance.RED

        val vision = OpenCVCamera()
        val detector = SkystoneDetector()
        vision.addTracker(detector)

        vision.initialize()

        while (!isStopRequested) {
            telemetry.addData("ordinal", SkystoneDetector.placeInt)
            telemetry.addData("place", SkystoneDetector.place)
            telemetry.update()
        }
    }
}