package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.lib.Globals.movementAllowed

@Config
class Grabber {
    var state = State.RELEASE

    private val frontServo = RevHubServo("grabberFront")
    private val backServo = RevHubServo("grabberBack")

    fun grab() {
        state = State.GRAB
    }

    fun release() {
        state = State.RELEASE
    }

    fun update() {
        if (movementAllowed) {
            val frontPos = state.frontPosition()
            val backPos = state.backPosition()
            frontServo.position = frontPos
            backServo.position = backPos
        }
    }

    enum class State(internal val frontPosition: () -> Double, internal val backPosition: () -> Double) {
        GRAB({ frontGrabPosition }, { backGrabPosition }),
        RELEASE({ frontReleasePosition }, { backReleasePosition }),
        LOAD({ frontReleasePosition}, { backGrabPosition})
    }

    companion object {
        @JvmField
        var frontGrabPosition = 0.0
        @JvmField
        var frontReleasePosition = 1.0
        @JvmField
        var backGrabPosition = 0.8
        @JvmField
        var backReleasePosition = 0.0
    }
}