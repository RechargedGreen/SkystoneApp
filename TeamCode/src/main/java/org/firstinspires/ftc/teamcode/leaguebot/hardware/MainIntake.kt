package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.hardware.rev.Rev2mDistanceSensor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.hMap
import org.firstinspires.ftc.teamcode.bulkLib.Go_3_7
import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.teleop.LeagueTeleOp
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode

@Config
class MainIntake {
    private val sensor = hMap.get(Rev2mDistanceSensor::class.java, "intakeSensor")

    private var readThisCycle = false
    private var sensorCache = 0.0

    private val stateTimer = ElapsedTime()

    val sensorTriggered get() = sensorDistance < sensorThreshold
    val sensorDistance: Double
        get() {
            if (!readThisCycle) {
                readThisCycle = true
                sensorCache = sensor.getDistance(DistanceUnit.INCH)
            }
            return sensorCache
        }

    enum class State(internal val hardwareState: HardwareStates) {
        STOP(HardwareStates.STOP),
        IN(HardwareStates.IN),
        OUT(HardwareStates.OUT),

        AUTO_INTAKE(HardwareStates.IN),
        FINISH_AUTO_INTAKE(HardwareStates.IN),
        AUTO_EJECT(HardwareStates.OUT)
    }

    enum class HardwareStates {
        STOP,
        IN,
        OUT
    }

    private val left = RevHubMotor("leftIntake", Go_3_7::class).openLoopControl.float
    private val right = RevHubMotor("rightIntake", Go_3_7::class).openLoopControl.float

    var state = State.STOP
        set(value) {
            field = value
            stateTimer.reset()
        }

    fun clearCache() {
        readThisCycle = false
    }

    val autoIntakeDone get() = state != State.AUTO_INTAKE

    fun update() {
        val power = when (state.hardwareState) {
            HardwareStates.STOP -> STOP_POWER
            HardwareStates.IN -> if(mode.isAutonomous) IN_POWER else 1.0
            HardwareStates.OUT -> OUT_POWER
        }

        when (state) {
            State.AUTO_INTAKE -> {
                lift.heightTarget = LeagueTeleOp.intakeHeight
                if (sensorTriggered)
                    state = State.FINISH_AUTO_INTAKE
            }

            State.FINISH_AUTO_INTAKE -> {
                if (stateTimer.seconds() > 0.3) {
                    state = State.AUTO_EJECT
                    lift.lower()
                }
            }

            State.AUTO_EJECT -> {
                if (stateTimer.seconds() > 1.0)
                    state = State.STOP
            }
            else -> {
            }
        }

        left.power = power
        right.power = power
    }

    companion object {
        @JvmField
        var IN_POWER = 0.7
        private const val STOP_POWER = 0.0
        private const val OUT_POWER = -1.0

        @JvmField
        var sensorThreshold = 2.0
    }
}