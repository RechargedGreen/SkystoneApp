package org.firstinspires.ftc.teamcode.ryanVision

fun VisionCamera.addTrackers(vararg trackers: Tracker) = trackers.forEach(::addTracker)