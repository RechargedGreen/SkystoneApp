package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.canvas.*
import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import org.firstinspires.ftc.teamcode.util.*

abstract class BaseMode(private val bot: BaseBot, val isAutonomous: Boolean) : LinearOpMode() {

    val movementAllowed: Boolean get() = isAutonomous || status != Status.INIT

    var stage = 0
        private set
    var changedStage = true
        private set
    private val stateChangeValidator = ChangeValidator(true)

    val fieldOverlay: Canvas
        get() = combinedPacket.packet.fieldOverlay()

    fun initializeStateVariables() {
        MovementAlgorithms.initAll()
    }

    fun nextStage(nextStage: Int = stage + 1, initStateVariables: Boolean = true) {
        if (stage != nextStage)
            forceStageChange()
        stage = nextStage
    }

    fun isTimedOut(seconds: Double) = stageTimer.seconds() > seconds

    fun forceStageChange(initStateVariables: Boolean = true) {
        forceStageChange()
        if (initStateVariables)
            initializeStateVariables()
    }

    fun timeoutStage(seconds: Double, nextStage: Int = stage + 1) {
        if (isTimedOut(seconds))
            nextStage(nextStage)
    }

    val stageTimer = ElapsedTime()
    private val runTimeTimer = ElapsedTime()

    lateinit var driver: Controller
    lateinit var operator: Controller

    lateinit var combinedPacket: CombinedPacket

    enum class Status {
        INIT,
        PLAY,
        STOP
    }

    val status: Status
        get() = when {
            isStopRequested -> Status.STOP
            isStarted       -> Status.PLAY
            else            -> Status.INIT
        }

    private var hasStarted = false

    final override fun runOpMode() {
        Globals.mode = this

        DriveMovement.resetForOpMode()

        GamePadMaster.reset()

        driver = Controller(gamepad1)
        operator = Controller(gamepad2)

        bot.setup()

        onInit()

        eventLoop@ while (true) {
            GamePadMaster.update()

            combinedPacket = CombinedPacket(telemetry)
            combinedPacket.put("OpModeStatus", status)

            when (status) {
                Status.INIT -> {
                    onInitLoop()
                }
                Status.PLAY -> {
                    if (hasStarted) {
                        changedStage = stateChangeValidator.validate()
                        onMainLoop()
                    } else {
                        if (isAutonomous)
                            AutomaticTeleopInit.transitionOnStop(this, bot.teleopName)
                        stageTimer.reset()
                        runTimeTimer.reset()
                        onStart()
                        hasStarted = true
                    }
                }
                Status.STOP -> {
                    break@eventLoop
                }
            }
            bot.update()
            combinedPacket.update()
        }

        onStop()
    }

    val secondsTillEnd: Double
        get() = (if (isAutonomous) 30.0 else 120.0) - secondsIntoMode
    val secondsIntoMode: Double
        get() = runTimeTimer.seconds()


    open fun onInit() {}
    open fun onInitLoop() {}
    open fun onStart() {}
    abstract fun onMainLoop()
    open fun onStop() {}
}