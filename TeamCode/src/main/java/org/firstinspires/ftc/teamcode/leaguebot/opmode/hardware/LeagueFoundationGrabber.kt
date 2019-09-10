package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

class LeagueFoundationGrabber {
    var grabbing = false

    fun grab() {
        grabbing = true
    }

    fun release() {
        grabbing = false
    }

    fun update() {

    }
}