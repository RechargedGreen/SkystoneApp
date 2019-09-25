package org.firstinspires.ftc.teamcode.lib

fun <T> ArrayList<T>.pop(index: Int): T {
    val v = get(index)
    removeAt(index)
    return v
}

fun <T> ArrayList<T>.popFirst(): T {
    return pop(0)
}

fun <T> ArrayList<T>.popLast(): T {
    return pop(size - 1)
}