package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.utils.linear.Matrix
import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import kotlin.math.*

object ScenePanel : JPanel() {
    private fun readResolve(): Any = ScenePanel

    private var scaleFactor = 500
    var lines: List<List<Vector>> = listOf()
        set(value) {
            field = value
            AxisLocatorPanel.rotationMatrix = rotationMatrix

            val minSize = min(width / 2, height / 2)
            scaleFactor = (minSize / BOX_RADIUS).toInt()
            repaint()
        }
    var color: Color = Color.WHITE

    private const val FOCUS_POSITION = -10f
    private const val ROTATION_SCALE_FACTOR = 0.01f
    private const val BOX_RADIUS = 2f
    private const val COLOR_STEP = 20f

    private var screenDistance = 10f

    private var rotationMatrix = Matrix.eye(4)

    private var mousePoint = Point(0, 0)
    private var isMousePressed = false

    init {
        background = Color.BLACK
        layout = FlowLayout(FlowLayout.LEFT)
        add(AxisLocatorPanel)
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                if (e == null) return
                mousePoint = e.point
                isMousePressed = true
            }

            override fun mouseReleased(e: MouseEvent?) {
                isMousePressed = false
            }
        })
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                if (e == null || !isMousePressed) return
                val dx = -(e.x - mousePoint.x).toFloat()
                val dy = -(e.y - mousePoint.y).toFloat()
                if (dx == 0f && dy == 0f) return

                val norm = sqrt(dx * dx + dy * dy)
                val angle = norm * ROTATION_SCALE_FACTOR
                rotateAroundVector(Vector.of(dy, -dx, 0f, norm) / norm, angle)

                mousePoint = e.point
                repaint()
            }
        })
        this.addMouseWheelListener(object : MouseAdapter() {
            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                if (e == null) return
                screenDistance += 0.1f * sign(e.wheelRotation.toFloat())
                screenDistance = max(screenDistance, 0f)
                repaint()
            }
        })
    }

    fun reset() {
        rotationMatrix = Matrix.eye(4)
        AxisLocatorPanel.rotationMatrix = rotationMatrix
        repaint()
    }

    private val projectionMatrix
        get() = Matrix.of(
            floatArrayOf(FOCUS_POSITION, 0f, 0f, 0f),
            floatArrayOf(0f, screenDistance, 0f, 0f),
            floatArrayOf(0f, 0f, screenDistance, 0f),
            floatArrayOf(1f, 0f, 0f, -FOCUS_POSITION)
        )

    internal fun rotateAroundVector(vector: Vector, angle: Float) {
        val c = cos(angle)
        val s = sin(angle)
        val (x, y, z) = vector
        rotationMatrix *= Matrix.of(
            floatArrayOf(c + (1 - c) * x * x, (1 - c) * x * y - s * z, (1 - c) * x * z + s * y, 0f),
            floatArrayOf((1 - c) * x * y + s * z, c + (1 - c) * y * y, (1 - c) * y * z - s * x, 0f),
            floatArrayOf((1 - c) * x * z - s * y, (1 - c) * y * z + s * x, c + (1 - c) * z * z, 0f),
            floatArrayOf(0f, 0f, 0f, 1f),
        )
        AxisLocatorPanel.rotationMatrix = rotationMatrix
    }

    private fun between(lower: Float, higher: Float, value: Float) = max(lower, min(value, higher))

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        val transformMatrix = projectionMatrix * rotationMatrix
        for (line in lines) {
            var prev = transformMatrix * line[0]
            var x = prev[0]
            prev = prev / prev[3]
            prev[0] = x
            for (i in 1..line.lastIndex) {
                val next = transformMatrix * line[i]
                x = next[0]
                next /= next[3]
                next[0] = x

                val p1y = (height / 2 - prev[1] * scaleFactor.toFloat()).toInt()
                val p1z = (width / 2 + prev[2] * scaleFactor.toFloat()).toInt()
                val p2y = (height / 2 - next[1] * scaleFactor.toFloat()).toInt()
                val p2z = (width / 2 + next[2] * scaleFactor.toFloat()).toInt()

                val p1Intensity = 0.5f + 1f / (2f * COLOR_STEP) * prev[0]
                val p2Intensity = 0.5f + 1f / (2f * COLOR_STEP) * next[0]

                var intensity = max(p1Intensity, p2Intensity)
                intensity = between(0f, 1f, intensity)
                (g as Graphics2D).stroke = BasicStroke(5 * intensity)

                val c = Color((color.red * intensity).toInt(), (color.green * intensity).toInt(),
                    (color.blue * intensity).toInt())

                g.color = c
                g.drawLine(p1z, p1y, p2z, p2y)
                prev = next
            }
        }
    }
}