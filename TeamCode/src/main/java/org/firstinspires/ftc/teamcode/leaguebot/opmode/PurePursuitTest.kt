package org.firstinspires.ftc.teamcode.leaguebot.opmode

import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.PurePursuit

class PurePursuitTest : LeagueBotAutoBase() {
    enum class Stage {
        A,
        B
    }

    override fun onMainLoop() {
        if (changedStage)
            PurePursuit.initForMove()
        when (Stage.values()[stage]) {
            Stage.A -> {
                val path = PurePursuit.Builder(0.0, 0.0, 0.0, 0.0, 0.0)
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                val curveComplete = PurePursuit.followCurve(path)
                if (curveComplete)
                    nextStage(Stage.B.ordinal)
            }
            Stage.B -> {
                val path = PurePursuit.Builder(0.0, 0.0, 0.0, 0.0, 0.0)
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                        .add(Point(0.0, 0.0))
                val curveComplete = PurePursuit.followCurve(path)
                if (curveComplete)
                    nextStage(Stage.A.ordinal)
            }
        }
    }
}