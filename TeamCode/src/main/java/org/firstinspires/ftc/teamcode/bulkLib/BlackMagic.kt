package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.internal.opmode.*
import org.firstinspires.ftc.robotcore.internal.system.*
import org.firstinspires.ftc.teamcode.lib.*

object BlackMagic {
    val opModeManager: OpModeManagerImpl
        get() = OpModeManagerImpl.getOpModeManagerOfActivity(AppUtil.getInstance().rootActivity)

    val hMap: HardwareMap get() = Globals.mode.hardwareMap
    /*val hMap: HardwareMap//todo fix
        get() = opModeManager.hardwareMap*/

    fun lynxModuleFromController(controller: LynxController): LynxModule {
        val moduleField = LynxController::class.java.getDeclaredField("module")
        moduleField.isAccessible = true
        return moduleField[controller] as LynxModule
    }

    fun controllerFromAnalogInput(input: AnalogInput): AnalogInputController {
        val controllerField = AnalogInput::class.java.getDeclaredField("controller")
        controllerField.isAccessible = true
        return controllerField[input] as AnalogInputController
    }

    fun channelFromAnalogInput(input: AnalogInput): Int {
        val channelField = AnalogInput::class.java.getDeclaredField("channel")
        channelField.isAccessible = true
        return channelField[input] as Int
    }

    fun controllerFromDigitalChannel(channel: DigitalChannel): DigitalChannelController {
        val controllerField = DigitalChannel::class.java.getDeclaredField("controller")
        controllerField.isAccessible = true
        return controllerField[channel] as DigitalChannelController
    }

    fun channelFromDigitalChannel(channel: DigitalChannel): Int {
        val channelField = DigitalChannel::class.java.getDeclaredField("channel")
        channelField.isAccessible = true
        return channelField[channel] as Int
    }

    fun lynxModuleFromIMU(imu: LynxEmbeddedIMU): LynxModule {
        return lynxModuleFromController(imu.deviceClient as LynxI2cDeviceSynch)

        /*val i2cDeviceSynchDevice = imu as I2cDeviceSynchDevice<I2cDeviceSynchImplOnSimple>

        val deviceClientField = I2cDeviceSynchDevice::class.java.getDeclaredField("deviceClient")
        deviceClientField.isAccessible = true
        val i2cDeviceSynchImplOnSimple = deviceClientField[i2cDeviceSynchDevice] as I2cDeviceSynchImplOnSimple

        val lynxControllerField = I2cDeviceSynchImplOnSimple::class.java.getDeclaredField("i2cDeviceSynchSimple")
        deviceClientField.isAccessible = true
        val lynxController = lynxControllerField[i2cDeviceSynchImplOnSimple] as LynxController

        return lynxModuleFromController(lynxController)*/
    }
}