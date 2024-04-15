package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.model.linear.Vector
import java.awt.*
import javax.swing.JPanel
import kotlin.math.*

object AxisLocatorPanel : JPanel() {
    private fun readResolve(): Any = AxisLocatorPanel

    private val axis = listOf(
        Vector.of(1f, 0f, 0f, 1f),
        Vector.of(0f, 1f, 0f, 1f),
        Vector.of(0f, 0f, 1f, 1f)
    )

    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
    private val names = listOf("x", "y", "z")

    private const val SCALE_FACTOR = 30

    private val rotationMatrix by ScenePanel::rotationMatrix

    private const val AXIS_WIDTH = 100
    private const val AXIS_HEIGHT = 100

    init {
        size = Dimension(AXIS_WIDTH * 2, AXIS_HEIGHT)
        preferredSize = Dimension(AXIS_WIDTH * 2, AXIS_HEIGHT)
        background = Color(0f, 0f, 0f, 0f)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        (g as Graphics2D).stroke = BasicStroke(1f)
        val r = rotationMatrix
        val alpha = atan2(r[2, 1], r[2, 2])
        val beta = atan2(-r[2, 0], sqrt(r[2, 1] * r[2, 1] + r[2, 2] * r[2, 2]))
        val gamma = atan2(r[1, 0], r[0, 0])

        val angles = floatArrayOf(alpha, beta, gamma)

        for (i in axis.indices) {
            g.color = colors[i]
            val transformed = rotationMatrix * axis[i]
            val x1 = AXIS_WIDTH / 2
            val y1 = AXIS_HEIGHT / 2
            val x2 = (AXIS_WIDTH / 2 + transformed[2] * SCALE_FACTOR).toInt()
            val y2 = (AXIS_HEIGHT / 2 - transformed[1] * SCALE_FACTOR).toInt()

            g.drawString(names[i], x2 + 3, y2 - 3)
            g.drawLine(x1, y1, x2, y2)

            g.drawString("${names[i]}: ${"%.2f".format(Math.toDegrees(angles[i].toDouble()))}°",
                AXIS_WIDTH, 10 + i * 20)
        }

    }
}