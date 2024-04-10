package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.utils.linear.Matrix
import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

object AxisLocatorPanel : JPanel() {
    private fun readResolve(): Any = AxisLocatorPanel

    private val axis = listOf(
        Vector.of(1f, 0f, 0f, 1f),
        Vector.of(0f, 1f, 0f, 1f),
        Vector.of(0f, 0f, 1f, 1f)
    )

    private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE)
    private val names = listOf("x", "y", "z")

    private const val scaleFactor = 30

    internal var rotationMatrix = Matrix.eye(4)

    init {
        size = Dimension(100, 100)
        preferredSize = Dimension(100, 100)
        background = Color.BLACK
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        (g as Graphics2D).stroke = BasicStroke(1f)
        for (i in axis.indices) {
            g.color = colors[i]
            val transformed = rotationMatrix * axis[i]
            val x1 = width / 2
            val y1 = height / 2
            val x2 = (width / 2 + transformed[2] * scaleFactor).toInt()
            val y2 = (height / 2 - transformed[1] * scaleFactor).toInt()

            g.drawString(names[i], x2 + 3, y2 - 3)
            g.drawLine(x1, y1, x2, y2)
        }
    }
}