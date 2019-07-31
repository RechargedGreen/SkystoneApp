package org.firstinspires.ftc.teamcode.roverBot.opMode.auto

import org.firstinspires.ftc.teamcode.roverBot.hardware.*
import org.firstinspires.ftc.teamcode.roverBot.opMode.*

class RoverGoldAuto : RoverAutoBase() {
    enum class MyStages {
        deploying,
        drivingToMarker,
        turningToMarker,
        deployingMarker,
        retracting,
        turnToSample,
        collectSample;

        companion object {
            operator fun get(ordinal: Int) = values()[ordinal]
        }
    }

    override fun onMainLoop() {
        when (MyStages[stage]) {
            MyStages.deploying       -> {
                RoverBot.lift.raise()
                if (RoverBot.lift.isUp()) {
                    nextStage()
                    RoverBot.lift.lower()
                }
            }

            MyStages.drivingToMarker -> {

            }

            MyStages.turningToMarker -> {

            }

            MyStages.deployingMarker -> {

            }

            MyStages.retracting      -> {

            }

            MyStages.turnToSample    -> {

            }

            MyStages.collectSample   -> {

            }
        }
    }
}