package org.firstinspires.ftc.teamcode.lib.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.lib.Globals
import kotlin.math.absoluteValue

class RevHubMotor(config: String, mode: DcMotor.RunMode, direction: DcMotorSimple.Direction, zeroPowerBehavior: DcMotor.ZeroPowerBehavior) {
    val delegate = Globals.hMap.get(DcMotorEx::class.java, config)

    var mode: DcMotor.RunMode? = null
        set(value) {
            if (field != value && value != null)
                delegate.mode = value
            field = value
        }

    var power = Double.NaN
        set(value) {
            if (!value.isNaN())
                if ((value - field).absoluteValue > 0.005 || (value == 0.0 && field != 0.0))
                    delegate.power = value
            field = value
        }

    var direction: DcMotorSimple.Direction? = null
        set(value) {
            if (field != value && value != null)
                delegate.direction = value
            field = value
        }

    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior? = null
        set(value) {
            if (field != value && value != null)
                delegate.zeroPowerBehavior = value
            field = value
        }

    init {
        this.mode = mode
        this.direction = direction
        this.zeroPowerBehavior = zeroPowerBehavior
    }
}