package ru.nsu.icg.wireframe.utils

import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.Color
import kotlin.math.*

class BSplineRotator(
    private var dots: List<Vector>,
    private var segmentsEnds: List<Vector>,
    private val m: Int,
    private val m1: Int,
) {
    private fun normalize() {
        var maxU = dots[0][0]
        var maxV = dots[0][0]
        for (dot in dots) {
            if (abs(dot[0]) > maxU) maxU = abs(dot[0])
            if (abs(dot[1]) > maxV) maxV = abs(dot[0])
        }
        val maxCoordinate = max(maxU, maxV)
        val newDots: MutableList<Vector> = mutableListOf()
        val newEnds: MutableList<Vector> = mutableListOf()
        for (dot in dots) {
            newDots.add(dot / maxCoordinate)
        }
        for (end in segmentsEnds) {
            newEnds.add(end / maxCoordinate)
        }
        dots = newDots
        segmentsEnds = newEnds
    }

    fun buildLines(): List<List<Vector>> {
        normalize()
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
            for (i in 0..<m * m1) {
                val angle: Float = i.toFloat() * 2f * PI.toFloat() / m / m1
                circle.add(Vector.of(end[1] * cos(angle), end[1] * sin(angle), end[0], 1f))
            }
            circle.add(circle[0])
            lines.add(circle)
        }
        return lines
    }

    companion object {
        private const val BOX_RADIUS = 1f
    }
}