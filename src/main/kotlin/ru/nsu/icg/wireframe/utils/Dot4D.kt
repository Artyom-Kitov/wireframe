package ru.nsu.icg.wireframe.utils

data class Dot4D(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
    operator fun plus(other: Dot4D) = Dot4D(x + other.x, y + other.y, z + other.z, w + other.w)
    operator fun minus(other: Dot4D) = Dot4D(x - other.x, y - other.y, z - other.z, w - other.w)
    operator fun times(scalar: Float) = Dot4D(x * scalar, y * scalar, z * scalar, w * scalar)
    operator fun div(scalar: Float) = this * (1 / scalar)
}