package ru.nsu.icg.wireframe.utils.linear

class Vector(
    private val vector: FloatArray
) {
    val n = vector.size

    companion object {
        fun of(vararg elements: Float) = Vector(elements)
    }

    operator fun get(i: Int) = vector[i]

    operator fun set(i: Int, value: Float) {
        vector[i] = value
    }

    operator fun plus(other: Vector) : Vector {
        require(n == other.n) { "different vector dimensions: $n and ${other.n}" }
        val newVector = floatArrayOf(*vector)
        for (i in 0..<n) {
            newVector[i] = vector[i] + other.vector[i]
        }
        return Vector(newVector)
    }

    operator fun plusAssign(other: Vector) {
        require(n == other.n) { "different vector dimensions: $n and ${other.n}" }
        for (i in 0..<n) {
            vector[i] += other.vector[i]
        }
    }

    operator fun minus(other: Vector) : Vector {
        require(n == other.n) { "different vector dimensions: $n and ${other.n}" }
        val newVector = floatArrayOf(*vector)
        for (i in 0..<n) {
            newVector[i] = vector[i] - other.vector[i]
        }
        return Vector(newVector)
    }

    operator fun minusAssign(other: Vector) {
        require(n == other.n) { "different vector dimensions: $n and ${other.n}" }
        for (i in 0..<n) {
            vector[i] -= other.vector[i]
        }
    }

    operator fun times(scalar: Float) : Vector {
        val newVector = floatArrayOf(*vector)
        for (i in 0..<n) {
            newVector[i] = vector[i] * scalar
        }
        return Vector(newVector)
    }

    operator fun timesAssign(scalar: Float) {
        for (i in 0..<n) {
            vector[i] *= scalar
        }
    }

    operator fun times(other: Vector) : Float {
        require(n == other.n) { "invalid dimensions: $n and ${other.n}" }
        var s = 0f
        for (i in vector.indices) {
            s += vector[i] * other.vector[i]
        }
        return s
    }

    operator fun div(scalar: Float) = this * (1f / scalar)

    operator fun divAssign(scalar: Float) = timesAssign(1f / scalar)

    operator fun timesAssign(matrix: Matrix) {
        val result = matrix * this
        for (i in vector.indices) {
            vector[i] = result.vector[i]
        }
    }

    operator fun component1() = vector[0]
    operator fun component2() = vector[1]
    operator fun component3() = vector[2]
    operator fun component4() = vector[3]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector

        if (!vector.contentEquals(other.vector)) return false
        if (n != other.n) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.contentHashCode()
        result = 31 * result + n
        return result
    }

    override fun toString() = vector.contentToString()
}