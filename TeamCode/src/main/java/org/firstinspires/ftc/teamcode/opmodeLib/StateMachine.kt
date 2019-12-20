package org.firstinspires.ftc.teamcode.opmodeLib

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.util.ChangeValidator

abstract class StateMachine {
    val stateChangeValidator = ChangeValidator(true)
    var changedStage = true
    var stage = 0

    private val stageTimer = ElapsedTime()

    val stageSeconds get() = stageTimer.seconds()
    fun isTimedOut(seconds:Double) = stageSeconds > seconds

    fun nextStage(nextStage:Int = stage + 1){
        stage = nextStage
        stageTimer.reset()
        onStateChange()
    }

    open fun onStateChange(){}

    fun reset(){
        nextStage(0)
        changedStage = true
        onReset()
    }

    open fun onReset(){

    }

    fun update():Boolean{
        changedStage = stateChangeValidator.validate()
        return onUpdate()
    }

    abstract fun onUpdate():Boolean
}