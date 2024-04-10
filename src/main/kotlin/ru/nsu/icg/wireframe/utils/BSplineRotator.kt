package ru.nsu.icg.wireframe.utils

import ru.nsu.icg.wireframe.utils.linear.Vector
import kotlin.math.*

class BSplineRotator(
    private var dots: List<Vector>,
    private var segmentsEnds: List<Vector>,
    private val m: Int,
    private val m1: Int,
) {
    private fun normalize(lines: List<List<Vector>>) {
        var maxCoordinate = abs(lines[0][0][0])
        for (line in lines) {
            for (dot in line) {
                for (i in 0..<dot.n) {
                    if (abs(dot[i]) > maxCoordinate) {
                        maxCoordinate = abs(dot[i])
                    }
                }
            }
        }
        for (line in lines) {
            for (dot in line) {
                dot /= maxCoordinate
                dot[2] = -dot[2]
                dot[3] = 1f
            }
        }
    }

    fun buildLines(): List<List<Vector>> {
        val lines: MutableList<MutableList<Vector>> = mutableListOf()
        for (spline in 0..<m) {
            val line: MutableList<Vector> = mutableListOf()
            for (dot in dots) {
                val angle: Float = spline.toFloat() * 2f * PI.toFloat() / m
                line.add(Vector.of(dot[1] * cos(angle), dot[1] * sin(angle), dot[0], 1f))
            }
            lines.add(line)
        }
        for (end in segmentsEnds) {
            val circle: MutableList<Vector> = mutableListOf()
            for (i in 0..m * m1) {
                val angle: Float = i.toFloat() * 2f * PI.toFloat() / m / m1
                circle.add(Vector.of(end[1] * cos(angle), end[1] * sin(angle), end[0], 1f))
            }
            lines.add(circle)
        }
        normalize(lines)
        return lines
    }
}