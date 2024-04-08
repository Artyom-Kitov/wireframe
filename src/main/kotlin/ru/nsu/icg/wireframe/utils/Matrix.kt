package ru.nsu.icg.wireframe.utils

import kotlin.math.sqrt

class Matrix(
    array: FloatArray
) {
    val n: Int = sqrt(array.size.toDouble()).toInt()

    private var matrix: FloatArray = array

    operator fun plus(other: Matrix): Matrix {
        val newArr = floatArrayOf(*other.matrix)
        for (i in 0..other.matrix.lastIndex) {
            newArr[i] = matrix[i] + other.matrix[i]
        }
        return Matrix(newArr)
    }

    operator fun plusAssign(other: Matrix) {
        for (i in 0..other.matrix.lastIndex) {
            matrix[i] += other.matrix[i]
        }
    }

    operator fun minus(other: Matrix): Matrix {
        val newArr = floatArrayOf(*other.matrix)
        for (i in 0..other.matrix.lastIndex) {
            newArr[i] = matrix[i] - other.matrix[i]
        }
        return Matrix(newArr)
    }

    operator fun minusAssign(other: Matrix) {
        for (i in 0..other.matrix.lastIndex) {
            matrix[i] -= other.matrix[i]
        }
    }

    operator fun times(other: Matrix) {
        TODO()
    }
}