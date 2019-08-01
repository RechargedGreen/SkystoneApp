package org.firstinspires.ftc.teamcode.bulkLib

import android.content.*
import com.qualcomm.hardware.lynx.*
import com.qualcomm.hardware.lynx.commands.core.*
import com.qualcomm.robotcore.eventloop.opmode.*

object BulkDataListener : OpModeManagerNotifier.Notifications {
    private val dataMap = HashMap<Int, BulkInputManager>()

    @OpModeRegistrar
    fun setupOpModeListenerOnStartRobot(context: Context, manager: AnnotatedOpModeManager) {
        BlackMagic.opModeManager.registerListener(this)
    }

    override fun onOpModePostStop(opMode: OpMode?) {
    }

    override fun onOpModePreInit(opMode: OpMode?) {
        dataMap.clear()
    }

    override fun onOpModePreStart(opMode: OpMode?) {
    }

    fun inputFrom(lynxModule: LynxModule): BulkInput {
        val address = lynxModule.moduleAddress
        if (!dataMap.containsKey(address))
            dataMap[address] = BulkInputManager(lynxModule)
        return dataMap[address]!!.cache
    }

    fun clearInputCaches() {
        for (m in dataMap.values)
            m.clearCache()
    }

    fun clearAllCaches() {
        clearInputCaches()
    }
}

val LynxModule.cachedInput: LynxGetBulkInputDataResponse
    get() = BulkDataListener.inputFrom(this).response

fun LynxGetBulkInputDataResponse.getAnalogInputVoltage(port: Int) = getAnalogInput(port) / 1000.0