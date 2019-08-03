package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.configuration.typecontainers.*
import org.firstinspires.ftc.robotcore.external.navigation.*
import kotlin.reflect.*

class RevHubMotor(controller: DcMotorController, portNumber: Int, direction: DcMotorSimple.Direction, motorType: MotorConfigurationType) : DcMotorImplEx(controller, portNumber, direction, motorType) {
    constructor(motor: DcMotor, configurationType: MotorConfigurationType) : this(motor.controller, motor.portNumber, motor.direction, configurationType)
    constructor(motor: DcMotor) : this(motor, motor.motorType)
    constructor(controller: DcMotorController, portNumber: Int, motorType: MotorConfigurationType) : this(controller, portNumber, DcMotorSimple.Direction.FORWARD, motorType)
    constructor(name: String) : this(BlackMagic.hMap.dcMotor[name])
    constructor(name: String, motorType: KClass<*>) : this(BlackMagic.hMap.dcMotor[name], MotorConfigurationType.getMotorType(motorType.java))

    private val exModule = BlackMagic.lynxModuleFromController(controller as LynxController)
    private val orientation = motorType.orientation

    private val powerCacher = WriteCacher<Double> { super.setPower(power) }
    private val modeCacher = WriteCacher<DcMotor.RunMode> { super.setMode(it) }

    private val velocityCacher = WriteCacher<Double> { super.setVelocity(it) }
    private val typeCacher = WriteCacher<MotorConfigurationType> { super.setMotorType(it) }
    private val positionPIDFCoefficientsCacher = WriteCacher<Double> { super.setPositionPIDFCoefficients(it) }
    private val targetPositionCacher = WriteCacher<Int> { super.setTargetPosition(it) }
    private val zeroPowerBehaviorCacher = WriteCacher<DcMotor.ZeroPowerBehavior> { super.setZeroPowerBehavior(it) }
    private val targetPositionToleranceCacher = WriteCacher<Int> { super.setTargetPositionTolerance(it) }
    private val enabledCacher = WriteCacher<Boolean> { if (it) super.setMotorEnable() else super.setMotorDisable() }


    val ticks_per_rev: Double
        get() = motorType.ticksPerRev

    override fun getCurrentPosition(): Int {
        val ticks = exModule.cachedInput.getEncoder(portNumber)
        return adjustForDirection(ticks)
    }

    override fun getVelocity(): Double {
        val ticksPerSecond = exModule.cachedInput.getVelocity(portNumber)
        return adjustForDirection(ticksPerSecond).toDouble()
    }

    override fun getVelocity(unit: AngleUnit?): Double {
        val ticksPerSecond = velocity
        val revsPerSecond = ticksPerSecond / ticks_per_rev
        return unit!!.unnormalized.fromDegrees(revsPerSecond * 360.0)
    }

    override fun setPower(power: Double) = powerCacher.write(power)
    override fun setDirection(direction: DcMotorSimple.Direction?) {}
    override fun setMode(mode: DcMotor.RunMode?) = modeCacher.write(mode)
    override fun setVelocity(angularRate: Double) = velocityCacher.write(angularRate)
    override fun setMotorType(motorType: MotorConfigurationType?) = typeCacher.write(motorType)
    override fun setPositionPIDFCoefficients(p: Double) = positionPIDFCoefficientsCacher.write(p)
    override fun setTargetPosition(position: Int) = targetPositionCacher.write(position)
    override fun setTargetPositionTolerance(tolerance: Int) = targetPositionToleranceCacher.write(tolerance)
    override fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) = zeroPowerBehaviorCacher.write(zeroPowerBehavior)
    override fun setMotorDisable() = enabledCacher.write(false)
    override fun setMotorEnable() = enabledCacher.write(true)

    private val verifyPIDMode = WriteVerifier<DcMotor.RunMode>()
    private val verifyPIDValues = WriteVerifier<PIDCoefficients>()
    override fun setPIDCoefficients(mode: DcMotor.RunMode?, pidCoefficients: PIDCoefficients?) {
        val modeChanged = verifyPIDMode.changed(mode)
        val valuesChanged = verifyPIDValues.changed(pidCoefficients)
        if (modeChanged || valuesChanged)
            super.setPIDCoefficients(mode, pidCoefficients)
    }

    private val verifyPIDFMode = WriteVerifier<DcMotor.RunMode>()
    private val verifyPIDFValues = WriteVerifier<PIDFCoefficients>()
    override fun setPIDFCoefficients(mode: DcMotor.RunMode?, pidfCoefficients: PIDFCoefficients?) {
        val modeChanged = verifyPIDFMode.changed(mode)
        val valuesChanged = verifyPIDFValues.changed(pidfCoefficients)
        if (modeChanged || valuesChanged)
            super.setPIDFCoefficients(mode, pidfCoefficients)
    }

    private val verifyVelocityUnit = WriteVerifier<DcMotor.RunMode>()
    private val verifyVelocityValue = WriteVerifier<Double>()
    override fun setVelocity(angularRate: Double, unit: AngleUnit?) {
        val unitChanged = verifyVelocityUnit.changed(mode)
        val valuesChanged = verifyVelocityValue.changed(angularRate)
        if (unitChanged || valuesChanged)
            super.setVelocity(angularRate, unit)
    }

    fun zeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior) = apply { this.zeroPowerBehavior = zeroPowerBehavior }
    fun direction(direction: DcMotorSimple.Direction) = apply { this.direction = direction }
    fun mode(mode: DcMotor.RunMode) = apply { this.mode = mode }

    fun BRAKE() = zeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
    fun FLOAT() = zeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT)
    fun VELO_PID() = mode(DcMotor.RunMode.RUN_USING_ENCODER)
    fun POSITION_PID() = mode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
    fun OPEN_LOOP() = mode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
    fun FORWARD() = direction(DcMotorSimple.Direction.FORWARD)
    fun REVERSE() = direction(DcMotorSimple.Direction.REVERSE)

    private fun adjustForDirection(v: Int) = if (operationalDirection == DcMotorSimple.Direction.REVERSE) -v else v
    private fun adjustForDirection(v: Double) = if (operationalDirection == DcMotorSimple.Direction.REVERSE) -v else v
}