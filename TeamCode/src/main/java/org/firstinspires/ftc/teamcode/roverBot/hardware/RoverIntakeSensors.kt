package org.firstinspires.ftc.teamcode.roverBot.hardware

import com.qualcomm.hardware.rev.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.teamcode.lib.*

class RoverIntakeSensors {
    private var searchingRight = true

    fun isLoaded(): Boolean {
        if (searchingRight) {
            if (rightLoaded()) {
                searchingRight = false
                return leftLoaded()
            }
        } else {
            if (leftLoaded()) {
                searchingRight = true
                return rightLoaded()
            }
        }

        return false
    }

    fun leftLoaded(): Boolean {
        if (!useLeftCache) {
            leftCache = leftSensor.getDistance(DistanceUnit.INCH)
            useLeftCache = true
        }
        return leftCache < leftThreshold
    }

    fun rightLoaded(): Boolean {
        if (!useRightCache) {
            rightCache = rightSensor.getDistance(DistanceUnit.INCH)
            useRightCache = true
        }
        return rightCache < rightThreshold
    }

    private val leftSensor = Globals.hMap.get(Rev2mDistanceSensor::class.java, "leftLoading")
    private val rightSensor = Globals.hMap.get(Rev2mDistanceSensor::class.java, "rightLoading")

    private var useLeftCache = false
    private var useRightCache = false
    private var leftCache = 0.0
    private var rightCache = 0.0

    var leftThreshold = 7.0
    var rightThreshold = 7.0
    val hz = 15.0

    private val timer = ElapsedTime()
    fun update() {
        if (timer.seconds() > 1.0 / hz) {
            useLeftCache = false
            useRightCache = false
            timer.reset()
        }
    }

}