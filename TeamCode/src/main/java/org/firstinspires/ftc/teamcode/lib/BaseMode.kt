package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class BaseMode(private val bot: BaseBot, val isAutonomous: Boolean) : LinearOpMode() {
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
        Globals.hMap = hardwareMap

        driver = Controller(gamepad1)
        operator = Controller(gamepad2)

        bot.setup()

        onInit()

        eventLoop@ while (true) {
            combinedPacket = CombinedPacket(telemetry)
            combinedPacket.put("OpModeStatus", status)

            when (status) {
                Status.INIT -> {
                    onInitLoop()
                }
                Status.PLAY -> {
                    if (hasStarted) {
                        onMainLoop()
                    } else {
                        onStart()
                        hasStarted = true
                    }
                }
                Status.STOP -> {
                    break@eventLoop
                }
            }
            bot.update()
        }

        onStop()
    }

    open fun onInit() {}
    open fun onInitLoop() {}
    open fun onStart() {}
    abstract fun onMainLoop()
    open fun onStop() {}
}