package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*

class RevHubDigitalSensor(controller: DigitalChannelController, private val channel: Int) : DigitalChannelImpl(controller, channel) {
    private val lynxController = controller as LynxDigitalChannelController
    private val exModule = BlackMagic.lynxModuleFromController(lynxController)

    val modeCacher = WriteCacher<DigitalChannel.Mode> { super.setMode(it) }
    val stateCacher = WriteCacher<Boolean> { super.setState(it) }

    override fun setMode(mode: DigitalChannel.Mode?) = modeCacher.write(mode)

    override fun setState(state: Boolean) {
        if (mode == DigitalChannel.Mode.OUTPUT)
            stateCacher.write(state)
    }

    override fun getState(): Boolean {
        if (mode == DigitalChannel.Mode.OUTPUT)
            return super.getState()
        return exModule.cachedInput.getDigitalInput(channel)
    }
}