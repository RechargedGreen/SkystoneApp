package org.firstinspires.ftc.teamcode.field

import org.firstinspires.ftc.teamcode.field.Field.SOUTH_WALL
import org.firstinspires.ftc.teamcode.opmodeLib.*
import org.firstinspires.ftc.teamcode.vision.*

enum class QuarryLocation {
    FAR_LEFT,
    FAR_MIDDLE,
    FAR_RIGHT,

    NEAR_LEFT,
    NEAR_MIDDLE,
    NEAR_RIGHT
}

class Stone(val index: Int) { // highest index
    var isSkystone = index % 3 == SkystoneDetector.placeInt
    var isEndNearBuildZone = index == 5
    var isEndFarFromBuildZone = index == 0

    val side_x = 24.0

    val center_x = side_x - WIDTH / 2.0
    var center_y = SOUTH_WALL + LENGTH / 2.0 + LENGTH * index

    companion object {
        const val WIDTH = 4.0
        const val LENGTH = 8.0

        const val BASE_HEIGHT = 4.0
        const val STUD = 1.0

        const val STUD_HEIGHT = BASE_HEIGHT + STUD
    }
}

object Quarry {

    val allStones = ArrayList<Stone>()

    operator fun get(index:Int) = allStones[index]
    operator fun get(location:QuarryLocation) = get(location.ordinal)

    private val stones = ArrayList<Stone>()
    private val skystones = ArrayList<Stone>()

    fun reset() {
        stones.clear()
        skystones.clear()
        for (i in 0 until 6) {
            val newStone = Stone(i)
            (if (newStone.isSkystone) skystones else stones).add(newStone)
            allStones.add(newStone)
        }
    }

    fun popStone(): Stone {
        if (doneWithSkystones())
            return stones.popLast()
        return skystones.popFirst()
    }

    fun doneWithStones(): Boolean = stones.isEmpty() && doneWithSkystones()
    fun doneWithSkystones(): Boolean = skystones.isEmpty()
}