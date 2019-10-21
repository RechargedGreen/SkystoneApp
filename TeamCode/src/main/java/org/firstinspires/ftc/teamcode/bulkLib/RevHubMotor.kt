import com.qualcomm.hardware.lynx.*
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.configuration.typecontainers.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.lynxModuleFromController
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*
import kotlin.reflect.*

class RevHubMotor(config: String, motorType: KClass<*>, hMap:HardwareMap = BlackMagic.hMap) {
    private val motor = hMap.dcMotor.get(config)
    private val port = motor.portNumber
    private val type = MotorConfigurationType.getMotorType(motorType.java)
    private val controller = (motor.controller as LynxDcMotorController).apply {
        setMotorType(motor.portNumber, type)
    }
    private val lynx = lynxModuleFromController(controller)

    val ticksPerRev = type.ticksPerRev
    val maxRPM = type.maxRPM

    private val orientationSign = when (type.orientation) {
        Rotation.CW, null -> 1
        Rotation.CCW      -> -1
    }

    var power: Double = 0.0
        set(value) {
            val clippedValue = Range.clip(value, -1.0, 1.0)
            if (clippedValue != field && (clippedValue == 0.0 || clippedValue.absoluteValue == 1.0 || clippedValue difference field > 0.005)) {
                field = value
                motor.power = value
            }
        }

    var targetPosition: Int = 0
        set(value) {
            if (field != value) {
                motor.targetPosition = value
                field = value
            }
        }

    val encoderTicks: Int get() = lynx.cachedInput.getEncoder(port) * orientationSign

    val reverse: RevHubMotor
        get() {
            direction = DcMotorSimple.Direction.REVERSE
            return this
        }

    val forward: RevHubMotor
        get() {
            direction = DcMotorSimple.Direction.FORWARD
            return this
        }

    val float: RevHubMotor
        get() {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
            return this
        }

    val brake: RevHubMotor
        get() {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            return this
        }

    val openLoopControl: RevHubMotor
        get() {
            mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            return this
        }

    val velocityControl: RevHubMotor
        get() {
            mode = DcMotor.RunMode.RUN_USING_ENCODER
            return this
        }

    val positionControl: RevHubMotor
        get() {
            mode = DcMotor.RunMode.RUN_TO_POSITION
            return this
        }

    var mode: DcMotor.RunMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        set(value) {
            if (field != value) {
                motor.mode = value
                field = value
            }
        }

    var zeroPowerBehavior = DcMotor.ZeroPowerBehavior.UNKNOWN
        set(value) {
            if (value != field) {
                if (value != DcMotor.ZeroPowerBehavior.UNKNOWN)
                    motor.zeroPowerBehavior = value
                field = value
            }
        }

    var direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD
        set(value) {
            if (value != field) {
                motor.direction = value
                field = value
            }
        }
}