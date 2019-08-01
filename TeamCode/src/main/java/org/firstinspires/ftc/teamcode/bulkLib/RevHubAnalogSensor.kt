package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*

/**
 * Created by David Lukens on 8/1/2019.
 */
open class RevHubAnalogSensor(controller: AnalogInputController, private val channel: Int) : AnalogInput(controller, channel) {
    constructor(input: AnalogInput) : this(BlackMagic.controllerFromAnalogInput(input), BlackMagic.channelFromAnalogInput(input))
    constructor(name: String) : this(BlackMagic.hMap.get(AnalogInput::class.java, name))

    private val exModule = BlackMagic.lynxModuleFromController(controller as LynxController)
    override fun getVoltage(): Double = exModule.cachedInput.getAnalogInputVoltage(channel)
}