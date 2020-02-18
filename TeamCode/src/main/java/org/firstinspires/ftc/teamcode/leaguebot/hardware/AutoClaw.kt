package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.movementAllowed

@Config
class AutoClaw {
    private val redClaw = Claw("redClaw_flip", "redClaw_claw")
    fun update() {
        if (movementAllowed) {
            redClaw.setSignal(state.redSignal())
        }
    }

    var state = State.STOW_STONE

    enum class State(internal val redSignal: () -> AutoClawSignal, internal val blueSignal: () -> AutoClawSignal) {
        GRABBING({ AutoClawSignal(redFlipGrab, redClawGrab) }, { AutoClawSignal(0.0, 0.0) }),
        STOW_RELEASE({ AutoClawSignal(redFlipStow, redClawRelease) }, { AutoClawSignal(0.0, 0.0) }),
        STOW_STONE({ AutoClawSignal(redFlipStow, redClawGrab) }, { AutoClawSignal(0.0, 0.0) }),
        PRE_GRAB({ AutoClawSignal(redFlipGrab, redClawRelease) }, { AutoClawSignal(0.0, 0.0) }),
        TELEOP({ AutoClawSignal(redFlipTeleop, redClawTeleop) }, {AutoClawSignal(0.0, 0.0)})
    }

    internal class Claw(flip: String, claw: String) {
        private val flip = RevHubServo(flip)
        private val claw = RevHubServo(claw)
        fun setSignal(signal: AutoClawSignal) {
            flip.position = signal.flip
            claw.position = signal.claw
        }
    }

    internal data class AutoClawSignal(val flip: Double, val claw: Double)

    companion object {
        @JvmField
        var redFlipStow = 0.357

        @JvmField
        var redFlipGrab = 1.0

        @JvmField
        var redClawGrab = 0.2

        @JvmField
        var redClawRelease = 0.57

        @JvmField
        var redClawTeleop = 0.5

        @JvmField
        var redFlipVertical = 0.5

        @JvmField
        var redFlipTeleop = 0.34
    }
}