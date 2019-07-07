package org.firstinspires.ftc.teamcode.util

class ChangeValidator(private var changed: Boolean) {
    fun trigger() {
        changed = true
    }

    fun validate(): Boolean {
        val c = changed
        changed = false
        return c
    }
}