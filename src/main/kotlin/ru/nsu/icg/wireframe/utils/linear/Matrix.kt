package ru.nsu.icg.wireframe.utils.linear

class Matrix(
    private val matrix: FloatArray,
    private val n: Int,
    private val m: Int,
) {
    companion object {
        fun of(vararg lines: FloatArray) : Matrix {
            val n = lines.size
            require(n > 0) { "invalid matrix size: $n" }
            val m = lines[0].size
            for (line in lines) {
                require(line.size == m) { "invalid line dimension: expected $m, found ${line.size}" }
            }

            val matrix = FloatArray(m * n)
            var currentIndex = 0
            for (line in lines) {
                for (num in line) {
                    matrix[currentIndex++] = num
                }
            }
            return Matrix(matrix, n, m)
        }

        fun eye(n: Int): Matrix {
            val matrix = FloatArray(n * n)
            for (i in 0..<n) {
                matrix[i * n + i] = 1f
            }
            return Matrix(matrix, n, n)
        }
    }

    operator fun get(line: Int, column: Int) = matrix[m * line + column]

    operator fun set(line: Int, column: Int, value: Float) {
        matrix[m * line + column] = value
    }

    operator fun plus(other: Matrix): Matrix {
        require(n == other.n && m == other.m) { "invalid dimensions: ($n, $m) vs (${other.n}, ${other.m})" }
        val newArr = floatArrayOf(*other.matrix)
        for (i in other.matrix.indices) {
            newArr[i] = matrix[i] + other.matrix[i]
        }
        return Matrix(newArr, n, m)
    }

    operator fun plusAssign(other: Matrix) {
        require(n == other.n && m == other.m) { "invalid dimensions: ($n, $m) vs (${other.n}, ${other.m})" }
        for (i in other.matrix.indices) {
            matrix[i] += other.matrix[i]
        }
    }

    operator fun minus(other: Matrix): Matrix {
        require(n == other.n && m == other.m) { "invalid dimensions: ($n, $m) vs (${other.n}, ${other.m})" }
        val newArr = floatArrayOf(*other.matrix)
        for (i in other.matrix.indices) {
            newArr[i] = matrix[i] - other.matrix[i]
        }
        return Matrix(newArr, n, m)
    }

    operator fun minusAssign(other: Matrix) {
        require(n == other.n && m == other.m) { "invalid dimensions: ($n, $m) vs (${other.n}, ${other.m})" }
        for (i in other.matrix.indices) {
            matrix[i] -= other.matrix[i]
        }
    }

    operator fun times(scalar: Float) : Matrix {
        val newArr = FloatArray(n * m)
        for (i in newArr.indices) {
            newArr[i] = matrix[i] * scalar
        }
        return Matrix(newArr, n, m)
    }

    operator fun timesAssign(scalar: Float) {
        for (i in matrix.indices) {
            matrix[i] *= scalar
        }
    }

    operator fun times(vector: Vector) : Vector {
        require(vector.n == m) { "invalid dimensions: ($n, $m) vs ${vector.n}" }
        val newVector = FloatArray(n)
        for (i in 0..<n) {
            var s = 0f
            for (j in 0..<m) {
                s += this[i, j] * vector[j]
            }
            newVector[i] = s
        }
        return Vector(newVector)
    }

    operator fun times(other: Matrix) : Matrix {
        require(m == other.n) { "invalid dimensions: ($n, $m) vs (${other.n}, ${other.m})" }
        val newMatrix = FloatArray(n * other.m)
        val result = Matrix(newMatrix, n, other.m)
        for (i in 0..<n) {
            for (j in 0..<other.m) {
                var s = 0f
                for (k in 0..<m) {
                    s += this[i, k] * other[k, j]
                }
                result[i, j] = s
            }
        }
        return result
    }

    override fun toString(): String {
        val builder = StringBuilder("[\n")
        for (i in 0..<n) {
            builder.append(matrix.copyOfRange(i * m, i * m + m).contentToString())
            builder.append(",\n")
        }
        builder.append("]")
        return builder.toString()
    }
}