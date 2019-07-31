package org.firstinspires.ftc.teamcode.util

import android.util.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.robotcontroller.internal.*
import org.firstinspires.ftc.robotcore.internal.opmode.*

object AutomaticTeleopInit : Thread() {
    init {
        this.start()
    }

    private var onStop: OpMode? = null
    private var transitionTo: String? = null
    private var opModeManager: OpModeManagerImpl? = null

    @Throws(InterruptedException::class)
    override fun run() {
        try {
            while (true) {
                synchronized(this) {
                    if (onStop != null && opModeManager?.activeOpMode != onStop) {
                        Thread.sleep(1000)
                        opModeManager?.initActiveOpMode(transitionTo)
                        reset()
                    }
                }
            }
            Thread.sleep(50)
        } catch (ex: InterruptedException) {
            Log.e(FtcRobotControllerActivity.TAG, "AutoTransitioner shutdown, thread interrupted")
        }
    }

    @Throws(InterruptedException::class)
    private fun reset() {
        onStop = null
        transitionTo = null
        opModeManager = null
    }

    @Throws(InterruptedException::class)
    private fun setNewTransition(onStop: OpMode, transitionTo: String) {
        synchronized(this) {
            this.onStop = onStop
            this.transitionTo = transitionTo
            this.opModeManager = onStop.internalOpModeServices as OpModeManagerImpl
        }
    }

    @Throws(InterruptedException::class)
    fun transitionOnStop(onStop: OpMode, transitionTo: String) {
        setNewTransition(onStop, transitionTo)
    }
}