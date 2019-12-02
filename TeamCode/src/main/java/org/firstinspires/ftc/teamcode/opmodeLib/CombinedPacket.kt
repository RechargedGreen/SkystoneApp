package org.firstinspires.ftc.teamcode.opmodeLib

import com.acmerobotics.dashboard.*
import com.acmerobotics.dashboard.telemetry.*
import org.firstinspires.ftc.robotcore.external.*

class CombinedPacket(val telemetry: Telemetry) {
    var packet = TelemetryPacket()
        private set

    fun addLine(line: String?) {
        packet.addLine(line)
        telemetry.addLine(line)
    }

    fun put(key: String?, value: Any?) {
        packet.put(key, value)
        telemetry.addData(key, value)
    }

    fun update() {
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        packet = TelemetryPacket()
        telemetry.update()
    }
}