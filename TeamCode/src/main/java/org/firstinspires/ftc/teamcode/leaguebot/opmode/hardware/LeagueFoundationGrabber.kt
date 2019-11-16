package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.lib.Globals
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE

@Config
class LeagueFoundationGrabber {
    //private val leftServo = RevHubServo("leftFoundation")
    //private val rightServo = RevHubServo("rightFoundation")


    enum class State(val leftDown: Boolean, val rightDown: Boolean) {
        left(true, false),
        right(false, true),
        both(true, true),
        none(false, false)
    }

    var state = State.none

    fun grab() {
        state = State.both
    }

    fun release() {
        state = State.none
    }

    fun grabWest() {
        if (ALLIANCE.isRed())
            grabLeft()
        else
            grabRight()
    }

    fun grabEast() {
        if (ALLIANCE.isRed())
            grabRight()
        else
            grabLeft()
    }

    fun grabLeft() {
        state = State.left
    }

    fun grabRight() {
        state = State.right
    }

    fun prepForGrab(){

    }

    fun update() {
        val l = if (state.leftDown) lDown else lUp
        val r = if (state.rightDown) rDown else rUp
        if (Globals.mode.movementAllowed) {
            //      leftServo.position = l
            //    rightServo.position = r
        }
    }

    companion object {
        @JvmField
        var lDown = 0.0
        @JvmField
        var rDown = 0.0
        @JvmField
        var lUp = 1.0
        @JvmField
        var rUp = 1.0
    }
}