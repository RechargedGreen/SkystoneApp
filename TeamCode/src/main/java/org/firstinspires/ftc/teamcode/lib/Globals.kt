package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.canvas.*
import com.acmerobotics.dashboard.config.*

object Globals {
    lateinit var mode: BaseMode

    val fieldOverlay: Canvas
        get() = mode.fieldOverlay

    @com.acmerobotics.dashboard.config.Config
    object Config {
        @JvmField
        var debugging = false // if we are in debugging mode we might make some changes like not going to park
    }
}