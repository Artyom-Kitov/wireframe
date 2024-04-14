package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.model.linear.Vector
import java.awt.*
import javax.swing.JPanel
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sign

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
        background = Color.BLACK
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        (g as Graphics2D).stroke = BasicStroke(1f)
        for (i in axis.indices) {
            g.color = colors[i]
            val transformed = rotationMatrix * axis[i]
            val x1 = AXIS_WIDTH / 2
            val y1 = AXIS_HEIGHT / 2
            val x2 = (AXIS_WIDTH / 2 + transformed[2] * SCALE_FACTOR).toInt()
            val y2 = (AXIS_HEIGHT / 2 - transformed[1] * SCALE_FACTOR).toInt()

            g.drawString(names[i], x2 + 3, y2 - 3)
            g.drawLine(x1, y1, x2, y2)

        }
        val beta = asin(rotationMatrix[0, 2]).toDouble()
        val gamma = asin(-rotationMatrix[0, 1] / cos(beta))
        val alpha = asin(-rotationMatrix[1, 2] / cos(beta))
        val angles = doubleArrayOf(alpha, beta, gamma)
        for (i in axis.indices) {
            g.color = colors[i]
            g.drawString("${names[i]}: ${"%.2f".format(Math.toDegrees(angles[i]))}°",
                AXIS_WIDTH, 10 + i * 20)
        }
    }
}