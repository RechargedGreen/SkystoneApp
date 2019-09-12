package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
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

        Imgproc.circle(mask0, Point(cx0, cy0), r, WHITE, Core.FILLED)
        Imgproc.circle(mask1, Point(cx1, cy1), r, WHITE, Core.FILLED)
        Imgproc.circle(mask2, Point(cx2, cy2), r, WHITE, Core.FILLED)

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
        overlay.strokeCircle(Point(cx0, cy0), r.toDouble(), WHITE, strokeWidth)
        overlay.strokeCircle(Point(cx1, cy1), r.toDouble(), WHITE, strokeWidth)
        overlay.strokeCircle(Point(cx2, cy2), r.toDouble(), WHITE, strokeWidth)
    }

    companion object {
        var placeInt = 0
            private set
        val place: SkystoneRandomization get() = SkystoneRandomization.getFromInt(placeInt)

        @JvmField
        var cx0 = 50.0
        @JvmField
        var cx1 = 150.0
        @JvmField
        var cx2 = 250.0

        @JvmField
        var cy0 = 100.0
        @JvmField
        var cy1 = 100.0
        @JvmField
        var cy2 = 100.0

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
            val list = values()
            val size = list.size - 1
            return list[if (ALLIANCE.isRed()) i else size - i]
        }
    }
}

class SkystoneRead(mat: Mat, val index: Int) {
    val score = Core.sumElems(mat).`val`.sum() // lower = skystone
}