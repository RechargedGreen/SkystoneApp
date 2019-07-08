package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import org.firstinspires.ftc.teamcode.movement.GoToPosition
import org.firstinspires.ftc.teamcode.movement.Path
import org.firstinspires.ftc.teamcode.util.ChangeValidator

@TeleOp
class PathStateMachineTest : LeagueBotAutoBase() {
    enum class State {
        P1,
        P2,
        P3,
        P4,
        CENTER
    }

    private var state = State.P1
        set(value) {
            if (value != field) {
                stateChangeValidator.trigger()
                stateTimer.reset()
                field = value
            }
        }
    private val stateChangeValidator = ChangeValidator(true)

    private val stateTimer = ElapsedTime()

    override fun onStart() {
        DriveMovement.setPosition(0.0, 0.0, 0.0)
    }

    override fun onMainLoop() {
        val changedState = stateChangeValidator.validate()
        when (state) {
            State.P1     -> {
                if (changedState)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    state = State.P2
            }
            State.P2     -> {
                if (changedState)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    state = State.P3
            }
            State.P3     -> {
                if (changedState)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    state = State.P4
            }
            State.P4     -> {
                if (changedState)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    state = State.P1
            }
            State.CENTER -> {
                if (changedState)
                    DriveMovement.path = Path(GoToPosition(0.0, 0.0, 0.0))
                if (DriveMovement.followSetPath())
                    DriveMovement.stopDrive()
                if (stateTimer.seconds() > 5.0)
                    state = State.P1
            }
        }

        when {
            driver.dUp    -> state = State.P1
            driver.dRight -> state = State.P2
            driver.dDown  -> state = State.P3
            driver.dLeft  -> state = State.P4
            driver.b      -> state = State.CENTER
        }
    }
}