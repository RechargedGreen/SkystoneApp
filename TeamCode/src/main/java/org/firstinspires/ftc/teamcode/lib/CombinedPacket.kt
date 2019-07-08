package org.firstinspires.ftc.teamcode.lib

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import org.firstinspires.ftc.robotcore.external.Telemetry

class CombinedPacket(private val telemetry: Telemetry) : TelemetryPacket() {
    override fun addLine(line: String?) {
        super.addLine(line)
        telemetry.addLine(line)
    }

    override fun put(key: String?, value: Any?) {
        super.put(key, value)
        telemetry.addData(key, value)
    }

    fun update() {
        FtcDashboard.getInstance().sendTelemetryPacket(this)
        telemetry.update()
    }
}