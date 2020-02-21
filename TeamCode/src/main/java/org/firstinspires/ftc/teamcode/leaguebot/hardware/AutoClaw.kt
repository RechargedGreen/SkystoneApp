package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.movementAllowed
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE

@Config
class AutoClaw {
    private val redClaw = Claw("redClaw_flip", "redClaw_claw")
    private val blueClaw = Claw("blueClaw_flip", "blueClaw_claw")
    fun update() {
        if (movementAllowed) {
            var redSignal = state.redSignal
            var blueSignal = state.blueSignal

            if (mode.isAutonomous) {
                if (ALLIANCE.isRed())
                    blueSignal = State.TELEOP.blueSignal
                else
                    redSignal = State.TELEOP.redSignal
            }

            if ((foundationGrabber.state != LeagueFoundationGrabber.State.up || !foundationGrabber.clearsClaw) && mode.secondsIntoMode > 0.6) {
                redSignal = State.VERTICAL.redSignal
                blueSignal = State.VERTICAL.blueSignal
            }

            redClaw.setSignal(redSignal())
            blueClaw.setSignal(blueSignal())
        }
    }

    var state = State.TELEOP

    enum class State(internal val redSignal: () -> AutoClawSignal, internal val blueSignal: () -> AutoClawSignal) {
        GRABBING({ AutoClawSignal(red_FlipGrab, red_ClawGrab) }, { AutoClawSignal(blue_FlipGrab, blue_ClawGrab) }),
        STOW_STONE({ AutoClawSignal(red_FlipStow, red_ClawGrab) }, { AutoClawSignal(blue_FlipStow, blue_ClawGrab) }),
        PRE_GRAB({ AutoClawSignal(red_FlipGrab, red_ClawRelease) }, { AutoClawSignal(blue_FlipGrab, blue_ClawRelease) }),
        VERTICAL({ AutoClawSignal(red_FlipVertical, red_ClawTeleop) }, { AutoClawSignal(blue_FlipVertical, blue_ClawTeleop) }),
        TELEOP({ AutoClawSignal(red_FlipTeleop, red_ClawTeleop) }, { AutoClawSignal(blue_FlipTeleop, blue_ClawTeleop) })
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
        var red_FlipStow = 0.28

        @JvmField
        var red_FlipGrab = 0.927

        @JvmField
        var red_ClawGrab = 0.67

        @JvmField
        var red_ClawRelease = 0.09

        @JvmField
        var red_ClawTeleop = 0.23

        @JvmField
        var red_FlipVertical = 0.5

        @JvmField
        var red_FlipTeleop = 0.22

        @JvmField
        var blue_FlipStow = 0.91

        @JvmField
        var blue_FlipGrab = 0.26

        @JvmField
        var blue_ClawGrab = 0.35

        @JvmField
        var blue_ClawRelease = 0.92

        @JvmField
        var blue_ClawTeleop = 0.8

        @JvmField
        var blue_FlipVertical = 0.72

        @JvmField
        var blue_FlipTeleop = 0.94
    }
}