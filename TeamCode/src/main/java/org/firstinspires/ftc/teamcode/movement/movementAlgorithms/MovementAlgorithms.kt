package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

object MovementAlgorithms {
    lateinit var movementProvider: MovementConstantsProvider
    fun initAll() {
        ToPositionAlgorithms.init()
        PurePursuit.init()
        PointControllers.init()
        AngleControllers.init()
    }
}

interface MovementConstantsProvider {
    // slippage prediction factors
    fun getXSlipFactor(): Double

    fun getYSlipFactor(): Double
    fun getTurnSlipFactor(): Double

    // minimum powers
    fun getMinY(): Double

    fun getMinX(): Double
    fun getMinTurn(): Double

    val slippageGoToPosProvider: SlippageAlgConstantsProvider
}

interface SlippageAlgConstantsProvider {
    val movement_speed: Double
    val turn_speed: Double

    val xProvider: SlippageAxisContantsProvider
    val yProvider: SlippageAxisContantsProvider
    val turnProvider: SlippageAxisContantsProvider

    val lateralThreshold: Double
    val degreesThreshold: Double
}

interface SlippageAxisContantsProvider {
    val endGunSlipScale: Double
    val endGunDistance: Double
    val endSlipSpeed: Double
    val switchBackToGunDistance: Double

    val fine_kP: Double
    val fineMaxSpeed: Double
}