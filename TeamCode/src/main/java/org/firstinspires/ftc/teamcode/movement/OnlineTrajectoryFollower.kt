package org.firstinspires.ftc.teamcode.movement

import com.acmerobotics.dashboard.config.*
import com.acmerobotics.roadrunner.control.*
import com.acmerobotics.roadrunner.path.*

@Config
object OnlineTrajectoryFollower {
    @JvmField
    var maxAccel = 30.0
    @JvmField
    var maxVel = 30.0
    @JvmField
    var kV = 1.0

    @JvmField
    var AXIS_PID = PIDCoefficients(0.0, 0.0, 0.0)
    @JvmField
    var HEADING_PID = PIDCoefficients(0.0, 0.0, 0.0)

    var profile = OnlineMotionProfile(0.0, maxAccel, maxVel)
        private set
    var path = Path(emptyList())
        private set

    fun setNewPath(path: Path) {
        OnlineTrajectoryFollower.path = path
        profile = OnlineMotionProfile(path.length(), maxAccel, maxVel)
    }

    fun update():Boolean{
        return true
    }

    private val axisController = PIDFController(AXIS_PID)
    private val headingController = PIDFController(AXIS_PID)
}