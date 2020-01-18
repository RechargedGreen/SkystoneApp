package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.movementAllowed

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
        LOAD({ frontLoadPosition}, { backGrabPosition}),
        PRE_LOAD({ frontReleasePosition}, { backGrabPosition}),
        FULL_RELEASE({frontFullReleasePosition}, { backReleasePosition})
    }

    companion object {
        @JvmField
        var frontGrabPosition = 0.0
        @JvmField
        var frontReleasePosition = 0.45
        @JvmField
        var frontFullReleasePosition = 1.0
        @JvmField
        var backGrabPosition = 0.8
        @JvmField
        var backReleasePosition = 0.0

        @JvmField
        var frontLoadPosition = 1.0
    }
}