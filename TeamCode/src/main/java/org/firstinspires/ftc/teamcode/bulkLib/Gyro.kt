package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.lynx.LynxModule
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.teamcode.movement.toDegrees

class OptimizedGyro(private val delegate: BNO055IMU, address: Int) {
    constructor(lynxModule: LynxModule) : this(LynxOptimizedI2cFactory.createLynxEmbeddedImu(lynxModule), lynxModule.moduleAddress)

    init {
        BulkDataMaster.putGyro(address, this)
        val params = BNO055IMU.Parameters()
        params.angleUnit = BNO055IMU.AngleUnit.RADIANS
        delegate.initialize(params)
    }

    private var cache: Orientation = Orientation()
        get() {
            if (!useCache) {
                field = delegate.angularOrientation
                useCache = true
            }
            return field
        }

    private var useCache = false

    fun clearCache() {
        useCache = false
    }

    val angle1_rad get() = cache.firstAngle.toDouble()
    val angle1_deg get() = angle1_rad.toDegrees
    val angle2_rad get() = cache.secondAngle.toDouble()
    val angle2_deg get() = angle2_rad.toDegrees
    val angle3_rad get() = cache.thirdAngle.toDouble()
    val angle3_deg get() = angle3_rad.toDegrees
}