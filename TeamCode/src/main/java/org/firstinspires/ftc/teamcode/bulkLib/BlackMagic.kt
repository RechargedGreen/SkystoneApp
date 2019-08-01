package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.internal.opmode.*
import org.firstinspires.ftc.robotcore.internal.system.*

object BlackMagic {
    val opModeManager: OpModeManagerImpl
        get() = OpModeManagerImpl.getOpModeManagerOfActivity(AppUtil.getInstance().rootActivity)

    val hMap: HardwareMap
        get() = opModeManager.hardwareMap

    fun lynxModuleFromController(controller: LynxController): LynxModule {
        val moduleField = LynxController::class.java.getDeclaredField("module")
        moduleField.isAccessible = true
        return moduleField[controller] as LynxModule
    }
}