package ru.nsu.icg.wireframe.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import ru.nsu.icg.wireframe.utils.linear.Matrix
import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.Color
import java.io.File
import java.io.IOException
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

    data class SerializedFigure(
        val figure: Figure,
        val rotation: Matrix,
        val screenDistance: Float,
        val rgb: Int
    )

    @Throws(IOException::class)
    fun writeTo(file: File, rotation: Matrix, screenDistance: Float, color: Color) {
        if (file.exists() && file.delete()) file.createNewFile()
        val gson = GsonBuilder().setPrettyPrinting().create()
        file.writeText(gson.toJson(SerializedFigure(this, rotation, screenDistance, color.rgb)))
    }

    companion object {
        @Throws(JsonParseException::class)
        fun readFrom(file: File): SerializedFigure {
            return Gson().fromJson(file.reader(), SerializedFigure::class.java)
        }

        val LOGO: Figure
        init {
            val stream = Figure::class.java.getResourceAsStream("/logo.json")
            val gson = Gson()
            LOGO = if (stream != null) {
                try {
                    gson.fromJson(stream.bufferedReader(), Figure::class.java)
                } catch (e: JsonParseException) {
                    e.printStackTrace()
                    Figure()
                }
            } else {
                Figure()
            }
            val size = LOGO.size
            val reflected = Figure()
            for (line in LOGO) {
                val reflectedLine = Line()
                for (dot in line) {
                    val arr = FloatArray(dot.n)
                    for (i in arr.indices) {
                        arr[i] = dot[i]
                    }
                    arr[0] *= -1f
                    reflectedLine.add(Vector(arr))
                }
                reflected.add(reflectedLine)
            }
            LOGO.addAll(reflected)
            for (i in 0..<size) {
                for (j in LOGO[i].indices) {
                    LOGO.add(Line(mutableListOf(
                        LOGO[i][j].copy(), reflected[i][j].copy()
                    )))
                }
            }
            LOGO.normalize()
        }
    }
}