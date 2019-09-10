package org.firstinspires.ftc.teamcode.vision

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.ryanVision.*

@TeleOp
class SkystoneVisionTest : LinearOpMode() {
    override fun runOpMode() {
        ALLIANCE = Alliance.RED

        val vision = VuforiaCamera()
        val detector = SkystoneDetector()
        vision.addTracker(detector)

        vision.initialize()

        while (!isStopRequested) {
            telemetry.addData("ordinal", detector.placeInt)
            telemetry.addData("place", detector.place)
            telemetry.update()
        }
    }
}