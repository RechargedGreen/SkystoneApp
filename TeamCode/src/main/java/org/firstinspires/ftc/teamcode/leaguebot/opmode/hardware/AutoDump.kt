package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.qualcomm.robotcore.util.*

object AutoDump {
    var isDone = true

    fun insertStone(){
        isDone = false
        nextStage()
    }

    fun startExtending(){

    }

    fun startDumping(){

    }

    enum class progStates{
        waiting,
        grab,
        extend,
        dump
    }

    private var stage = 0
    private var desiredStage = 0

    private val timer = ElapsedTime()
    fun nextStage(){
        timer.reset()
    }
}