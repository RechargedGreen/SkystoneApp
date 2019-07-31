package org.firstinspires.ftc.teamcode.ryanVision

import org.opencv.core.*

abstract class TrackerShortcut : Tracker() {
    override fun init(camera: VisionCamera) {}
    override fun processFrame(frame: Mat, timestamp: Double) {}
    override fun drawOverlay(overlay: Overlay, imageWidth: Int, imageHeight: Int, debug: Boolean) {}
}