package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.hardware.lynx.commands.core.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.internal.system.*

/**
 * Created by David Lukens on 8/1/2019.
 */
object LynxOptimizedI2cFactory {
    private class BetterI2cDeviceSynchImplOnSimple(simple: I2cDeviceSynchSimple, isSimpleOwned: Boolean) : I2cDeviceSynchImplOnSimple(simple, isSimpleOwned) {
        override fun setReadWindow(window: I2cDeviceSynch.ReadWindow?) {
            // do nothing
        }
    }

    fun createLynxI2cDeviceSynch(module: LynxModule, bus: Int): I2cDeviceSynch = BetterI2cDeviceSynchImplOnSimple(LynxFirmwareVersionManager.createLynxI2cDeviceSynch(AppUtil.getDefContext(), module, bus), true)
    fun createLynxEmbeddedImu(module: LynxModule) = LynxEmbeddedIMU(createLynxI2cDeviceSynch(module, 0))
}