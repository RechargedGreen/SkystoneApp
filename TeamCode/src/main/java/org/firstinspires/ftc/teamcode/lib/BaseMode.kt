package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.canvas.Canvas
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.bulkLib.BulkDataMaster
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.setAngle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms
import org.firstinspires.ftc.teamcode.ryanVision.OpenCVCamera
import org.firstinspires.ftc.teamcode.util.AutomaticTeleopInit
import org.firstinspires.ftc.teamcode.util.ChangeValidator
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector

abstract class BaseMode(private val bot: BaseBot, val isAutonomous: Boolean, private val alliance: Alliance?, val position_mirror: Pose?) : LinearOpMode() {
    private val camera = OpenCVCamera()


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
        stageTimer.reset()
    }

    fun nextStage(nextStage: Int = stage + 1, initStateVariables: Boolean = true) {
        /*if (stage != nextStage)*/
        forceStageChange()
        stage = nextStage
    }

    fun isTimedOut(seconds: Double) = stageTimer.seconds() > seconds

    fun forceStageChange(initStateVariables: Boolean = true) {
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
            isStarted -> Status.PLAY
            else -> Status.INIT
        }

    private var hasStarted = false

    final override fun runOpMode() {
        Globals.mode = this

        if (alliance != null)
            RunData.ALLIANCE = alliance

        DriveMovement.resetForOpMode()

        BulkDataMaster.reset()

        GamePadMaster.reset()

        driver = Controller(gamepad1)
        operator = Controller(gamepad2)

        if (isAutonomous) {
            camera.addTracker(SkystoneDetector())
            camera.initialize()
        }

        bot.setup()

        onInit()

        eventLoop@ while (true) {
            GamePadMaster.update()

            BulkDataMaster.clearAllCaches()

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
                        camera.close()
                        position_mirror?.apply {
                            world_x_mirror = point.x
                            world_y_mirror = point.y
                            setAngle_mirror(heading)
                        }
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