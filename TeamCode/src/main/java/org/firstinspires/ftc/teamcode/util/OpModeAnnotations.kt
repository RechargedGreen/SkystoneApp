package org.firstinspires.ftc.teamcode.util

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class RechargedAuto(
        val name: String,
        val group: String = ".9999999")

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class RechargedTeleop(
        val name: String,
        val group: String = ".9999999")