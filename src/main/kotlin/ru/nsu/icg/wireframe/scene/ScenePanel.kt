package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.utils.linear.Matrix
import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel
import kotlin.math.cos
import kotlin.math.sin

object ScenePanel : JPanel() {
    private fun readResolve(): Any = ScenePanel

    private const val SCALE_FACTOR = 1000
    var lines: List<List<Vector>> = listOf()
        set(value) {
            field = value
            angleX = 0f
            angleY = 0f
            angleZ = 0f
            repaint()
        }
    var color: Color = Color.WHITE

    private const val CAM_POSITION = -10f
    private const val FOCUS_DISTANCE = 10f
    private val projectionMatrix = Matrix.of(
        floatArrayOf(1f, 0f, 0f, 0f),
        floatArrayOf(0f, FOCUS_DISTANCE, 0f, 0f),
        floatArrayOf(0f, 0f, FOCUS_DISTANCE, -0f),
        floatArrayOf(1f, 0f, 0f, FOCUS_DISTANCE - CAM_POSITION)
    )

    private var angleX = 0f
    private var angleY = 0f
    private var angleZ = 0f

    init {
        background = Color.BLACK
    }

    fun rotate(angleX: Float, angleY: Float, angleZ: Float) {
        this.angleX += angleX
        this.angleY += angleY
        this.angleZ += angleZ
        repaint()
    }

    private fun rotationMatrixX(angle: Float) = Matrix.of(
        floatArrayOf(1f, 0f, 0f, 0f),
        floatArrayOf(0f, cos(angle), -sin(angle), 0f),
        floatArrayOf(0f, sin(angle), cos(angle), 0f),
        floatArrayOf(0f, 0f, 0f, 1f)
    )

    private fun rotationMatrixY(angle: Float) = Matrix.of(
        floatArrayOf(cos(angle), 0f, sin(angle), 0f),
        floatArrayOf(0f, 1f, 0f, 0f),
        floatArrayOf(-sin(angle), 0f, cos(angle), 0f),
        floatArrayOf(0f, 0f, 0f, 1f)
    )

    private fun rotationMatrixZ(angle: Float) = Matrix.of(
        floatArrayOf(cos(angle), -sin(angle), 0f, 0f),
        floatArrayOf(sin(angle), cos(angle), 0f, 0f),
        floatArrayOf(0f, 0f, 1f, 0f),
        floatArrayOf(0f, 0f, 0f, 1f)
    )

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        g.color = color
        val transformMatrix = projectionMatrix * (rotationMatrixX(angleX) *
                rotationMatrixY(angleY) *
                rotationMatrixZ(angleZ))
        for (line in lines) {
            var prev = transformMatrix * line[0]
            prev = prev / prev[3]
            for (i in 1..line.lastIndex) {
                val next = transformMatrix * line[i]
                next /= next[3]

                val p1y = height / 2 - prev[1] * SCALE_FACTOR.toFloat()
                val p1z = width / 2 - prev[2] * SCALE_FACTOR.toFloat()
                val p2y = height / 2 - next[1] * SCALE_FACTOR.toFloat()
                val p2z = width / 2 - next[2] * SCALE_FACTOR.toFloat()

                g.drawLine(p1z.toInt(), p1y.toInt(), p2z.toInt(), p2y.toInt())
                prev = next
            }
        }
    }
}