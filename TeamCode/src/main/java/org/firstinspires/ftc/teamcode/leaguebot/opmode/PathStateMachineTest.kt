package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import org.firstinspires.ftc.teamcode.movement.GoToPosition
import org.firstinspires.ftc.teamcode.movement.Path

@TeleOp
class PathStateMachineTest : LeagueBotAutoBase() {
    enum class Stage {
        P1,
        P2,
        P3,
        P4,
        CENTER;
    }

    fun nextState(stage: Stage) = nextStage(stage.ordinal)
    fun timeoutStage(seconds: Double, stage: Stage) = timeoutStage(seconds, stage.ordinal)

    override fun onStart() {
        DriveMovement.setPosition(0.0, 0.0, 0.0)
    }

    override fun onMainLoop() {
        telemetry.addData("Stage", Stage.values()[stage])
        when (Stage.values()[stage]) {
            Stage.P1     -> {
                if (changedStage)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    nextState(Stage.P2)
            }
            Stage.P2     -> {
                if (changedStage)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    nextState(Stage.P3)
            }
            Stage.P3     -> {
                if (changedStage)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    nextState(Stage.P4)
            }
            Stage.P4     -> {
                if (changedStage)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    nextState(Stage.P1)
            }
            Stage.CENTER -> {
                if (changedStage)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    DriveMovement.stopDrive()
                timeoutStage(5.0, Stage.P1)
            }
        }

        when {
            driver.dUp    -> nextState(Stage.P1)
            driver.dRight -> nextState(Stage.P2)
            driver.dDown  -> nextState(Stage.P3)
            driver.dLeft  -> nextState(Stage.P4)
            driver.b      -> nextState(Stage.CENTER)
        }
    }
}