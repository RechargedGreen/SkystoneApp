package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.path

import com.acmerobotics.roadrunner.geometry.*

class PathFollower(private val cruiseVelocity: Double = 7.0) {
    private var startTime = 0.0
    private var stage = progStages.DONE

    enum class progStages {
        ACCELERATE,
        CRUISE,
        FINE_TUNE,
        DONE,
        CHANGE_PATH
    }

    fun setNewPath(newPath: PathContainer) {
        pathContainer = newPath
        stage = progStages.CHANGE_PATH
    }

    private lateinit var pathContainer: PathContainer
    private lateinit var trajectory: DisplacementTrajectory

    fun getDriveVel(): Pose2d {
        when (stage) {
            progStages.ACCELERATE  -> {

            }
            progStages.CRUISE      -> {

            }
            progStages.CHANGE_PATH -> {
                return if (pathContainer.hasNewPath) {
                    trajectory = pathContainer.nextTrajectory
                    stage = progStages.ACCELERATE
                    getDriveVel()
                } else {
                    stage = progStages.DONE
                    Pose2d()
                }
            }
            progStages.FINE_TUNE   -> {

            }
            progStages.DONE        -> {
                return Pose2d()
            }
        }
        return Pose2d()
    }

    val isDone get() = stage == progStages.DONE
}