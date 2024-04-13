package ru.nsu.icg.wireframe.utils

import ru.nsu.icg.wireframe.utils.linear.Vector
import java.io.File
import java.nio.file.Path
import kotlin.math.*

class BSplineRotator(
    private var dots: List<Vector>,
    private var segmentsEnds: List<Vector>,
    private val m: Int,
    private val m1: Int,
) {
    fun buildLines(): Figure {
        val figure = Figure()
        for (spline in 0..<m) {
            val line = Line()
            for (dot in dots) {
                val angle: Float = spline.toFloat() * 2f * PI.toFloat() / m
                line.add(Vector.of(dot[1] * cos(angle), dot[1] * sin(angle), dot[0], 1f))
            }
            figure.add(line)
        }
        for (end in segmentsEnds) {
            val circle = Line()
            for (i in 0..m * m1) {
                val angle: Float = i.toFloat() * 2f * PI.toFloat() / m / m1
                circle.add(Vector.of(end[1] * cos(angle), end[1] * sin(angle), end[0], 1f))
            }
            figure.add(circle)
        }
        figure.normalize()
        return figure
    }
}