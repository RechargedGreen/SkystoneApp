package org.firstinspires.ftc.teamcode.ryanVision

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

fun Mat.setRGBToBlack() = setTo(RGBScalers.BLACK)

fun Mat.iterateSquare(square: Square, task: (x: Int, y: Int, mat: Mat) -> Unit): Int {
    val center = square.center
    val radius = square.radius
    val xStart = center.x.toInt() - radius
    val xEnd = center.x.toInt() + radius
    val yStart = center.y.toInt() - radius
    val yEnd = center.y.toInt() + radius

    var pixelCount = 0

    val w = width()
    val h = height()

    for (x in xStart..xEnd) {
        for (y in yStart..yEnd) {
            if (x < w && x > -1 && y < h && y > -1) {
                task(x, y, this)
                pixelCount++
            }
        }
    }

    return pixelCount
}

fun Mat.iteratePixelsInSquare(square: Square, task: (pixel: DoubleArray) -> Unit) = iterateSquare(square) { x, y, mat ->
    task(mat.get(y, x))
}

fun Mat.averageChannelsInSquare(square: Square): List<Double> {
    val sums = DoubleArray(getAtPoint(square.center).size)

    val pxCount = iteratePixelsInSquare(square) { pixel ->
        pixel.indices.forEach { i ->
            sums[i] += pixel[i]
        }
    }

    return if (pxCount == 0) sums.toList() else sums.map { it / pxCount }
}

fun Mat.getAtPoint(p: Point) = get(p.y.toInt(), p.x.toInt())

data class Square(val center: Point, val radius: Int) {
    private val x = center.x
    private val y = center.y

    val rect = Rect(Point(x - radius, y - radius),
                    Point(x + radius, y + radius))

    val size = radius * radius * 4.0
}