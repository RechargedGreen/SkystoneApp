package org.firstinspires.ftc.teamcode.sharedhardware

import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.lib.Globals.mode

class OdometryPuller(private val up:()->Double, private val down:()->Double) {
    private val servo = RevHubServo("odometryServo")
    fun update(){
        if(mode.movementAllowed)
            servo.position = if(mode.isAutonomous) down() else up()
    }
}