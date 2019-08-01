package org.firstinspires.ftc.teamcode.bulkLib

class WriteCacher<T>(private val write: (T) -> Unit) {
    private var cache: T? = null

    fun write(value: T?) {
        if (cache != value) {
            write(value)
            cache = value
        }
    }
}

class WriteVerifier<T> {
    private var cache: T? = null
    fun changed(value: T?): Boolean {
        if (value != cache) {
            cache = value
            return true
        }
        return false
    }
}