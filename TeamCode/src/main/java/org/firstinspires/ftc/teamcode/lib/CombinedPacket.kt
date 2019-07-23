package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.firstinspires.ftc.robotcore.external.Telemetry

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