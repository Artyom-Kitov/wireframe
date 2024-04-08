package ru.nsu.icg.wireframe.utils

data class Dot2D(
    val u: Float,
    val v: Float
) {
    operator fun plus(other: Dot2D) = Dot2D(u + other.u, v + other.v)
    operator fun minus(other: Dot2D) = Dot2D(u - other.u, v - other.v)
    operator fun times(scalar: Float) = Dot2D(u * scalar, v * scalar)
    operator fun div(scalar: Float) = this * (1 / scalar)
}