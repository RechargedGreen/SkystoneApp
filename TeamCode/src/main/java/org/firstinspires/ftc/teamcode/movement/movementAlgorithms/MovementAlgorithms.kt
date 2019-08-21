package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

object MovementAlgorithms {
    lateinit var movementProvider: MovementConstantsProvider
    fun initAll() {
        PurePursuit.init()
        ToPositionAlgorithms.init()
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
}