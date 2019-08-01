package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.hardware.lynx.commands.core.*

class BulkInputManager(val module: LynxModule) {
    private var useCache = false
    var cache: BulkInput = null!!
        private set
        get() {
            if (!useCache) {
                val newCache = BulkInput(module)
                if (newCache.initiatedProperly)
                    cache = newCache
                useCache = true
            }
            return cache
        }

    fun clearCache() {
        useCache = false
    }
}

class BulkInput(module: LynxModule) : LynxCommExceptionHandler() {
    var response: LynxGetBulkInputDataResponse
        private set
    var initiatedProperly = false

    init {
        try {
            response = LynxGetBulkInputDataCommand(module).sendReceive()
            initiatedProperly = true
        } catch (e: Exception) {
            response = null!!
            handleException(e)
        }
    }
}