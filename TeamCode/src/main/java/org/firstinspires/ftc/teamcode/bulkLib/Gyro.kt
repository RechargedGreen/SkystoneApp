package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.bosch.*
import com.qualcomm.hardware.lynx.*
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.teamcode.movement.*

class OptimizedGyro(lynxModule: LynxModule, mounting: Mounting) {
    constructor(name: String, mounting: Mounting) : this(BlackMagic.lynxModuleFromIMU(BlackMagic.hMap[BNO055IMU::class.java, name] as LynxEmbeddedIMU), mounting)
    constructor(mounting: Mounting) : this("imu", mounting)

    private val delegate: BNO055IMU = LynxOptimizedI2cFactory.createLynxEmbeddedImu(lynxModule)

    init {
        BulkDataMaster.putGyro(lynxModule.moduleAddress, this)
        val params = BNO055IMU.Parameters()
        params.angleUnit = BNO055IMU.AngleUnit.RADIANS
        delegate.initialize(params)
    }

    enum class Mounting {
        VERTICAL,
        FLAT
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

    val rawHeading: Angle
        get() = Angle.createUnwrappedRad(-cache.firstAngle.toDouble())

    var heading: Angle
        get() = rawHeading + headingBias
        set(value) {
            headingBias = value - rawHeading
        }

    var heading_rad: Double
        get() = heading.rad
        set(value) {
            heading = Angle.createWrappedRad(value)
        }

    var heading_deg: Double
        get() = heading.deg
        set(value) {
            heading = Angle.createUnwrappedDeg(value)
        }

    private var headingBias = Angle.createUnwrappedRad(0.0)
}