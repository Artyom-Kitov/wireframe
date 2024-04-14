package ru.nsu.icg.wireframe.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import ru.nsu.icg.wireframe.model.linear.Matrix
import ru.nsu.icg.wireframe.model.linear.Vector
import java.awt.Color
import java.io.File
import java.io.IOException

data class Scene(
    val figure: Figure = Figure(),
    val rotation: Matrix = Matrix.eye(4),
    val screenDistance: Float = 10f,
    val rgb: Int = Color.MAGENTA.rgb,
    val horizontalRotation: Vector = Vector.of(0f, 0f, 1f, 1f),
    val verticalRotation: Vector = Vector.of(0f, 1f, 0f, 1f),
) {
    @Throws(IOException::class)
    fun writeTo(file: File) {
        if (file.exists() && file.delete()) file.createNewFile()
        val gson = GsonBuilder().setPrettyPrinting().create()
        file.writeText(gson.toJson(this))
    }

    companion object {
        @Throws(JsonParseException::class)
        fun readFrom(file: File): Scene {
            val scene = Gson().fromJson(file.bufferedReader(), Scene::class.java)
            for (line in scene.figure) {
                for (dot in line) {
                    if (dot.n != 4) throw JsonParseException("invalid figure")
                }
            }
            if (scene.horizontalRotation.n != 4 || scene.verticalRotation.n != 4) {
                throw JsonParseException("invalid axis")
            }
            if (scene.rotation.n != 4 || scene.rotation.m != 4) {
                throw JsonParseException("invalid rotation")
            }
            if (scene.screenDistance < 0f) {
                return scene.copy(screenDistance = 0f)
            }
            return scene
        }

        val LOGO: Scene
        init {
            val stream = Scene::class.java.getResourceAsStream("/logo.json")
            val gson = Gson()
            LOGO = if (stream != null) {
                try {
                    gson.fromJson(stream.bufferedReader(), Scene::class.java)
                } catch (e: JsonParseException) {
                    Scene()
                }
            } else {
                Scene()
            }
            val size = LOGO.figure.size
            val reflected = Figure()
            for (line in LOGO.figure) {
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
            LOGO.figure.addAll(reflected)
            for (i in 0..<size) {
                for (j in LOGO.figure[i].indices) {
                    LOGO.figure.add(Line(mutableListOf(
                        LOGO.figure[i][j].copy(), reflected[i][j].copy()
                    )))
                }
            }
            LOGO.figure.normalize()
        }
    }
}