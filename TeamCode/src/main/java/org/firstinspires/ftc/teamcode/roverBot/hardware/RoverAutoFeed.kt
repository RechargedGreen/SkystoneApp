package org.firstinspires.ftc.teamcode.roverBot.hardware

import com.qualcomm.robotcore.util.*

class RoverAutoFeed {
    enum class MyStates {
        waiting,
        retractingExtension,
        exchanging,
        lifting,
        dumping,
        lowering,

        disabled
    }

    enum class FeedType(val dumpDelay: () -> Double, val waitForTwo: Boolean) {
        normal({ normalDelay }, true),
        sample({ sampleDelay }, false),
        onemineral({ normalDelay }, false)
    }

    companion object {
        var sampleDelay = 0.5
        var normalDelay = 0.5
        var maxExchangeTime = 1.0
    }

    var state = MyStates.waiting
    var changedState = true
    var stateTimer = ElapsedTime()
    var currentFeedType = FeedType.normal

    fun update() {
        when (state) {
            MyStates.waiting             -> {
                RoverBot.dumper.setDumping(false)
            }
            MyStates.retractingExtension -> {
                RoverBot.dumper.setDumping(false)
                RoverBot.lift.lower()
                RoverBot.extension.retract()

                RoverBot.intake.stop()

                if (RoverBot.extension.isIn() && RoverBot.lift.isDown())
                    nextStage(MyStates.exchanging)
            }
            MyStates.exchanging          -> {
                RoverBot.dumper.setDumping(false)
                RoverBot.lift.lower()
                RoverBot.extension.retract()

                RoverBot.intake.feed()

                if ((if (currentFeedType.waitForTwo) RoverBot.dumper.isLoaded() else RoverBot.dumper.loadedOne()) || stateTimer.seconds() > maxExchangeTime)
                    nextStage(MyStates.lifting)
            }
            MyStates.lifting             -> {
                RoverBot.dumper.setDumping(false)
                RoverBot.lift.raise()
                RoverBot.extension.retract()

                RoverBot.intake.stop()
            }
            MyStates.dumping             -> {
                RoverBot.dumper.setDumping(true)
                RoverBot.lift.lower()
                RoverBot.extension.retract()

                RoverBot.intake.stop()

                if (stateTimer.seconds() > currentFeedType.dumpDelay())
                    nextStage(MyStates.lowering)
            }
            MyStates.lowering            -> {
                RoverBot.dumper.setDumping(false)
                RoverBot.lift.lower()
                if (RoverBot.lift.isDown())
                    nextStage(MyStates.waiting)
            }
        }
        changedState = false
    }

    fun tryTriggeringDump() {
        if (RoverBot.lift.isUp() && state == MyStates.lifting)
            nextStage(MyStates.dumping)
    }

    fun setDisabled(disabled: Boolean) {
        if (disabled)
            nextStage(MyStates.disabled)
        else if (state == MyStates.disabled)
            nextStage(MyStates.waiting)
    }

    fun callNormalFeed() = callFeed(FeedType.normal)

    private fun callFeed(feedType: FeedType) {
        if (state == MyStates.waiting)
            nextStage(MyStates.retractingExtension)
    }

    fun callOneMineralFeed() = callFeed(FeedType.onemineral)
    fun callSampleFeed() = callFeed(FeedType.sample)

    fun abortFeed() {
        if (state != MyStates.disabled)
            nextStage(MyStates.waiting)
    }

    fun callIfLoaded() {
        if (state == MyStates.waiting)
            if (RoverBot.intakeLoadingSensors.isLoaded())
                callNormalFeed()
    }

    fun nextStage(newState: MyStates) {
        state = newState
        changedState = true
        stateTimer.reset()
    }

    fun doneWithAutoFeed() = state == MyStates.waiting || state == MyStates.disabled || state == MyStates.lowering
}