package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.hardware.*

@Config
object Globals {
    lateinit var mode: BaseMode
    lateinit var hMap: HardwareMap

    @JvmField
    var debugging = false // if we are in debugging mode we might make some changes like not going to park
}