package org.firstinspires.ftc.teamcode.lib.hardware

import com.qualcomm.robotcore.hardware.configuration.annotations.*
import org.firstinspires.ftc.robotcore.external.navigation.*

@MotorType(ticksPerRev = 537.6, gearing = 19.2, maxRPM = 312.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "ActualRev20", xmlTagAliases = ["ActualRev20"], name = "actual_rev_20", builtIn = false)
interface ActualRev20

@MotorType(ticksPerRev = 5264.0, gearing = 188.0, maxRPM = 30.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_188:1", xmlTagAliases = ["Go_188:1"], name = "Go_188:1", builtIn = false)
interface Go_188

@MotorType(ticksPerRev = 3892.0, gearing = 139.0, maxRPM = 43.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_139:1", xmlTagAliases = ["Go_139:1"], name = "Go_139:1", builtIn = false)
interface Go_139

@MotorType(ticksPerRev = 2786.0, gearing = 99.5, maxRPM = 60.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_99.5:1", xmlTagAliases = ["Go_99.5:1"], name = "Go_99.5:1", builtIn = false)
interface Go_99_5

@MotorType(ticksPerRev = 1993.6, gearing = 71.2, maxRPM = 84.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_71.2:1", xmlTagAliases = ["Go_71.2:1"], name = "Go_71.2:1", builtIn = false)
interface Go_71_2

@MotorType(ticksPerRev = 1425.2, gearing = 50.9, maxRPM = 117.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_50.9:1", xmlTagAliases = ["Go_50.9:1"], name = "Go_50.9:1", builtIn = false)
interface Go_50_9

@MotorType(ticksPerRev = 753.2, gearing = 26.9, maxRPM = 223.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_26.9:1", xmlTagAliases = ["Go_26.9:1"], name = "Go_26.9:1", builtIn = false)
interface Go_26_9

@MotorType(ticksPerRev = 537.6, gearing = 19.2, maxRPM = 312.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_19.2:1", xmlTagAliases = ["Go_19.2:1"], name = "Go_19.2:1", builtIn = false)
@ExpansionHubPIDFVelocityParams(P = 1.17, I = 0.117, F = 11.7)
@ExpansionHubPIDFPositionParams(P = 5.0)
interface Go_19_2

@MotorType(ticksPerRev = 383.6, gearing = 13.7, maxRPM = 435.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_13.7:1", xmlTagAliases = ["Go_13.7:1"], name = "Go_13.7:1", builtIn = false)
interface Go_13_7

@MotorType(ticksPerRev = 145.6, gearing = 5.2, maxRPM = 1150.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_5.2:1", xmlTagAliases = ["Go_5.2:1"], name = "Go_5.2:1", builtIn = false)
interface Go_5_2

@MotorType(ticksPerRev = 103.6, gearing = 3.7, maxRPM = 1620.0, orientation = Rotation.CW, achieveableMaxRPMFraction = 1.0)
@DeviceProperties(xmlTag = "Go_3.7:1", xmlTagAliases = ["Go_3.7:1"], name = "Go_3.7:1", builtIn = false)
interface Go_3_7