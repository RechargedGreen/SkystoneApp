package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.opmodeLib.*
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.ryanVision.*
import org.firstinspires.ftc.teamcode.ryanVision.RGBScalers.BLACK
import org.firstinspires.ftc.teamcode.ryanVision.RGBScalers.WHITE
import org.opencv.core.*
import org.opencv.imgproc.*

@Config
class SkystoneDetector : Tracker() {

    private lateinit var mat0: Mat
    private lateinit var mat1: Mat
    private lateinit var mat2: Mat

    private lateinit var mask0: Mat
    private lateinit var mask1: Mat
    private lateinit var mask2: Mat

    private var madeMats = false

    val x_0: Double get() = if (ALLIANCE == Alliance.RED) r_x0 else b_x0
    val x_1: Double get() = if (ALLIANCE == Alliance.RED) r_x1 else b_x1
    val x_2: Double get() = if (ALLIANCE == Alliance.RED) r_x2 else b_x2
    val y:Double get() = if(ALLIANCE == Alliance.RED) r_y else b_y
    override fun init(camera: VisionCamera) {
        mat0 = Mat()
        mat1 = Mat()
        mat2 = Mat()
    }

    override fun processFrame(frame: Mat, timestamp: Double) {
        val (h, w) = (frame.height() to frame.width())
        val type = frame.type()
        if (!madeMats) {
            mask0 = Mat(h, w, type)
            mask1 = Mat(h, w, type)
            mask2 = Mat(h, w, type)
            madeMats = true
        }

        mask0.setTo(BLACK)
        mask1.setTo(BLACK)
        mask2.setTo(BLACK)

        Imgproc.circle(mask0, Point(x_0, y), r, WHITE, Core.FILLED)
        Imgproc.circle(mask1, Point(x_1, y), r, WHITE, Core.FILLED)
        Imgproc.circle(mask2, Point(x_2, y), r, WHITE, Core.FILLED)

        Core.bitwise_and(mask0, frame, mat0)
        Core.bitwise_and(mask1, frame, mat1)
        Core.bitwise_and(mask2, frame, mat2)

        val results = ArrayList<SkystoneRead>()
        results.add(SkystoneRead(mat0, 0))
        results.add(SkystoneRead(mat1, 1))
        results.add(SkystoneRead(mat2, 2))

        results.sortByDescending { it.score }

        placeInt = results.last().index
    }

    override fun drawOverlay(overlay: Overlay, imageWidth: Int, imageHeight: Int, debug: Boolean) {
        overlay.strokeCircle(Point(x_0, y), r.toDouble(), WHITE, strokeWidth)
        overlay.strokeCircle(Point(x_1, y), r.toDouble(), WHITE, strokeWidth)
        overlay.strokeCircle(Point(x_2, y), r.toDouble(), WHITE, strokeWidth)
    }

    companion object {
        var placeInt = 0
            private set
        val place: SkystoneRandomization get() = SkystoneRandomization.getFromInt(placeInt)

        /// red

        @JvmField var r_y = 45.0
        @JvmField var b_y = 45.0

        @JvmField
        var r_x0 = 35.0
        @JvmField
        var r_x1 = 110.0
        @JvmField
        var r_x2 = 185.0

        /// blue

        @JvmField
        var b_x0 = 240.0
        @JvmField
        var b_x1 = 160.0
        @JvmField
        var b_x2 = 80.0
        //////////////

        @JvmField
        var r = 10

        @JvmField
        var strokeWidth = 3
    }
}


enum class SkystoneRandomization {
    FAR,
    MID,
    NEAR;  // nearest building zone

    companion object {
        fun getFromInt(i: Int): SkystoneRandomization {
            return values()[i]
        }
    }
}

class SkystoneRead(mat: Mat, val index: Int) {
    val score = Core.sumElems(mat).`val`.sum() // lower = skystone
}