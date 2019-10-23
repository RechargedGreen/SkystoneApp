package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.bosch.*
import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.teamcode.movement.*
import kotlin.experimental.and

class OptimizedGyro(private val delegate:BNO055IMU, address:Int, private val mounting:Mounting) {
    constructor(lynxModule: LynxModule, mounting: Mounting) : this(LynxOptimizedI2cFactory.createLynxEmbeddedImu(lynxModule), lynxModule.moduleAddress, mounting)
    constructor(name: String, mounting: Mounting, hMap: HardwareMap = BlackMagic.hMap) : this(BlackMagic.lynxModuleFromIMU(hMap[BNO055IMU::class.java, name] as LynxEmbeddedIMU), mounting)
    constructor(mounting: Mounting, hMap:HardwareMap = BlackMagic.hMap) : this("imu", mounting, hMap)

    init {
        BulkDataMaster.putGyro(address, this)
        val params = BNO055IMU.Parameters()
        params.angleUnit = BNO055IMU.AngleUnit.RADIANS

        val AXIS_MAP_CONFIG_BYTE: Byte = 0x6 // swap x and z
        val AXIS_MAP_SIGN_BYTE: Byte = 0x1 // negate z

        if(mounting == Mounting.VERTICAL){
            try {
                delegate.write8(BNO055IMU.Register.OPR_MODE, (com.qualcomm.hardware.bosch.BNO055IMU.SensorMode.CONFIG.bVal and 0x0F).toInt())// swap axes
                Thread.sleep(100)

                delegate.write8(BNO055IMU.Register.AXIS_MAP_CONFIG, (AXIS_MAP_CONFIG_BYTE and 0x0F).toInt())// swap axes
                delegate.write8(BNO055IMU.Register.AXIS_MAP_SIGN, (AXIS_MAP_SIGN_BYTE and 0x0F).toInt())// negate axis

                val s = BNO055IMU.SensorMode.IMU.bVal
                delegate.write8(BNO055IMU.Register.OPR_MODE, (s and 0x0F).toInt()) // swap back to sensor mode

                Thread.sleep(100)
            }
            catch (e:InterruptedException){
                Thread.currentThread().interrupt()
            }
        }

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