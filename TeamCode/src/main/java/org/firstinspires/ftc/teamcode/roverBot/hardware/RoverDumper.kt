package org.firstinspires.ftc.teamcode.roverBot.hardware

class RoverDumper {
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

    private var wantToDump = false

    fun setDumping(dumping: Boolean) {
        wantToDump = dumping
    }

    fun loadedOne(): Boolean {
        if (leftLoaded())
            return true
        if (rightLoaded())
            return true
        return false
    }

    fun leftLoaded() = false
    fun rightLoaded() = false
}