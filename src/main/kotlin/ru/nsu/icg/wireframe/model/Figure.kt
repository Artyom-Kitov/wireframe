package ru.nsu.icg.wireframe.model

import kotlin.math.abs

data class Figure(
    private val lines: MutableList<Line> = mutableListOf()
) : MutableList<Line> by lines {
    fun normalize() {
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
                dot[3] = 1f
            }
        }
    }
}