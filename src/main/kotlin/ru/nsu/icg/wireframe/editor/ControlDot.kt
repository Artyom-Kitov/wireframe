package ru.nsu.icg.wireframe.editor

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class ControlDot (
    val index: Int,
    var u: Float,
    var v: Float
) : JPanel() {
    var onDrag: (ControlDot) -> Unit = { _ -> run {} }
    var onSelect: (ControlDot) -> Unit = { _ -> run {} }
    var isSelected = false

    private var isPressed = false
    private var prevX = 0
    private var prevY = 0

    init {
        val controlDot = this
        size = Dimension(2 * DOT_RADIUS + 1, 2 * DOT_RADIUS + 1)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                onSelect(controlDot)
            }

            override fun mousePressed(e: MouseEvent?) {
                super.mousePressed(e)
                if (e == null) {
                    return
                }
                onSelect(controlDot)
                isPressed = true
                prevX = e.x
                prevY = e.y
            }

            override fun mouseReleased(e: MouseEvent?) {
                super.mouseReleased(e)
                isPressed = false
            }
        })
        val dot = this
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                super.mouseDragged(e)
                if (e == null) {
                    return
                }
                if (!isPressed) {
                    return
                }
                val dx = e.x - prevX
                val dy = e.y - prevY
                setLocation(location.x + dx, location.y + dy)
                onDrag(dot)
            }
        })
    }

    override fun paintComponent(g: Graphics?) {
        if (g == null) {
            return
        }
        g.color = if (isSelected) DOT_SELECTED_COLOR else DOT_UNSELECTED_COLOR
        g.drawOval(0, 0, 2 * DOT_RADIUS, 2 * DOT_RADIUS)
        g.fillOval(
            DOT_RADIUS - DOT_THICKNESS / 2, DOT_RADIUS - DOT_THICKNESS / 2,
            DOT_THICKNESS, DOT_THICKNESS
        )
        (g as Graphics2D).drawString(index.toString(), DOT_RADIUS, DOT_RADIUS - 5)
    }

    override fun setLocation(x: Int, y: Int) {
        super.setLocation(x - DOT_RADIUS, y - DOT_RADIUS)
    }

    override fun getLocation(): Point {
        return Point(super.getLocation().x + DOT_RADIUS, super.getLocation().y + DOT_RADIUS)
    }

    companion object {
        private const val DOT_RADIUS = 15
        private const val DOT_THICKNESS = 4
        val DOT_UNSELECTED_COLOR: Color = Color.RED
        val DOT_SELECTED_COLOR: Color = Color.GREEN
    }
}