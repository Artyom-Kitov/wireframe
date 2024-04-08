package ru.nsu.icg.wireframe.editor

import org.springframework.stereotype.Component
import ru.nsu.icg.wireframe.utils.Dot2D
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs

@Component
class EditorPanel : JPanel() {
    var splineColorSupplier: () -> Color = { Color.WHITE }
    var nSupplier: () -> Int = { 10 }

    var onSelect: (ControlDot) -> Unit = {  }
    var onUnselect: () -> Unit = {  }

    private val controlDots: MutableList<ControlDot> = mutableListOf()
    private val splineDots = mutableListOf<Dot2D>()

    private val zoomFactor = 1.1f

    private var scaleFactor = 150
    private var biasU: Float = 0f
    private var biasV: Float = 0f
    private var selectedDot: ControlDot? = null
    private var dotCounter = 1

    private var isPressed = false
    private var origin = Point(0, 0)

    init {
        isDoubleBuffered = true
        background = Color.BLACK
        layout = null
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e == null) {
                    return
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    selectedDot?.isSelected = false
                    selectedDot = null
                    onUnselect()
                    repaint()
                } else {
                    addDot(xToU(e.x), yToV(e.y))
                }
            }

            override fun mousePressed(e: MouseEvent?) {
                if (e == null) return
                isPressed = true
                origin = Point(e.x, e.y)
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (e == null) return
                isPressed = false
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
            }
        })
        this.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                if (e == null || !isPressed) {
                    return
                }
                val dx = e.x - origin.x
                val dy = e.y - origin.y
                biasU -= dx.toFloat() / scaleFactor
                biasV -= dy.toFloat() / scaleFactor
                origin = Point(e.x, e.y)
                repaint()
            }
        })
        this.addMouseWheelListener(object : MouseAdapter() {
            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                if (e == null) {
                    return
                }
                if (e.wheelRotation > 0) {
                    onZoom(1 / zoomFactor)
                } else {
                    onZoom(zoomFactor)
                }
            }
        })
    }

    fun addDot(u: Float, v: Float) {
        val dot = ControlDot(dotCounter++, u, v)
        controlDots.add(dot)
        add(dot)
        dot.setLocation(uToX(u), vToY(v))
        dot.onDrag = ::onDotDragged
        dot.onSelect = {
            onDotSelected(it)
            onSelect(it)
        }
        dot.onSelect(dot)
        repaint()
    }

    fun setDotCoordinates(dot: ControlDot, u: Float, v: Float) {
        dot.setLocation(uToX(u), vToY(v))
        dot.u = u
        dot.v = v
        repaint()
    }

    fun onDotsShown(b: Boolean) {
        controlDots.forEach{ it.isVisible = b }
        repaint()
    }

    fun onDotDelete() {
        if (selectedDot == null) {
            return
        }
        controlDots.remove(selectedDot)
        remove(selectedDot)
        selectedDot = null
        repaint()
    }

    fun onZoom(factor: Float = zoomFactor) {
        if (scaleFactor * factor < 1f) {
            return
        }
        scaleFactor = (scaleFactor * factor).toInt()
        rescale()
        repaint()
    }

    private fun rescale() {
        controlDots.forEach {
            it.setLocation(uToX(it.u), vToY(it.v))
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) {
            return
        }
        rescale()
        paintGrid(g)
        paintBSpline(g)
    }

    private fun paintGrid(g: Graphics) {
        g.color = LINE_COLOR
        val biasX = (biasU * scaleFactor).toInt()
        val biasY = (biasV * scaleFactor).toInt()
        g.drawLine(0, height / 2 - biasY, width, height / 2 - biasY)
        g.drawLine(width / 2 - biasX, 0, width / 2 - biasX, height)

        val serifSize = 4
        val sizeX = width / scaleFactor + abs(biasX)
        val sizeY = height / scaleFactor + abs(biasY)
        for (i in -sizeX + 1..<sizeX) {
            g.drawLine(width / 2 + i * scaleFactor - biasX, height / 2 - serifSize - biasY,
                width / 2 + i * scaleFactor - biasX, height / 2 + serifSize - biasY)
            (g as Graphics2D).drawString(i.toString(), width / 2 + i * scaleFactor + serifSize - biasX,
                height / 2 - 2 * serifSize - biasY)
        }
        for (i in -sizeY + 1..<sizeY) {
            g.drawLine(width / 2 - serifSize - biasX, height / 2 + i * scaleFactor - biasY,
                width / 2 + serifSize - biasX, height / 2 + i * scaleFactor - biasY)
            if (i != 0) {
                (g as Graphics2D).drawString((-i).toString(), width / 2 + 2 * serifSize - biasX,
                    height / 2 + i * scaleFactor + 2 * serifSize - biasY)
            }
        }
        (g as Graphics2D).drawString("u", width - 20, height / 2 + 20 - biasY)
        g.drawString("v", width / 2 + 10 - biasX, 10)

        if (controlDots.isEmpty() || !controlDots[0].isVisible) {
            return
        }
        g.color = ControlDot.DOT_UNSELECTED_COLOR
        for (i in 0..<controlDots.lastIndex) {
            val x1 = controlDots[i].location.x
            val y1 = controlDots[i].location.y
            val x2 = controlDots[i + 1].location.x
            val y2 = controlDots[i + 1].location.y
            g.drawLine(x1, y1, x2, y2)
        }
    }

    private fun paintBSpline(g: Graphics) {
        if (controlDots.size < 2) {
            return
        }
        val dots = controlDots
        g.color = splineColorSupplier()
        val n = nSupplier()

        splineDots.clear()
        val last = dots.lastIndex - 2
        for (i in 1..last) {
            val uArr = arrayOf(dots[i - 1].u, dots[i].u, dots[i + 1].u,
                dots[i + 2].u)
            val vArr = arrayOf(dots[i - 1].v, dots[i].v, dots[i + 1].v,
                dots[i + 2].v)
            val coefsU = coefficients(uArr)
            val coefsV = coefficients(vArr)
            val dt = 1f / n
            for (j in 0..< if (i == last) n + 1 else n) {
                val t = j * dt
                val u = coefsU[0] * t * t * t + coefsU[1] * t * t + coefsU[2] * t + coefsU[3]
                val v = coefsV[0] * t * t * t + coefsV[1] * t * t + coefsV[2] * t + coefsV[3]
                splineDots.add(Dot2D(u, v))
            }
        }
        for (i in 0..<splineDots.lastIndex) {
            val prevU = splineDots[i].u
            val prevV = splineDots[i].v
            val u = splineDots[i + 1].u
            val v = splineDots[i + 1].v
            g.drawLine(uToX(prevU), vToY(prevV), uToX(u), vToY(v))
        }
    }

    private fun coefficients(p: Array<Float>): Array<Float> {
        val a = (-p[0] + 3 * p[1] - 3 * p[2] + p[3]) / 6f
        val b = (3 * p[0] - 6 * p[1] + 3 * p[2]) / 6f
        val c = (-3 * p[0] + 3 * p[2]) / 6f
        val d = (p[0] + 4 * p[1] + p[2]) / 6f
        return arrayOf(a, b, c, d)
    }

    private fun onDotDragged(dot: ControlDot) {
        val newX = dot.location.x
        val newY = dot.location.y
        dot.u = xToU(newX)
        dot.v = yToV(newY)
        onSelect(dot)
        repaint()
    }

    private fun onDotSelected(dot: ControlDot) {
        selectedDot?.isSelected = false
        selectedDot = dot
        dot.isSelected = true
        repaint()
    }

    private fun uToX(u: Float): Int = (width / 2 + (u - biasU) * scaleFactor).toInt()
    private fun vToY(v: Float): Int = (height / 2 + (v - biasV) * scaleFactor).toInt()
    private fun xToU(x: Int): Float = (x.toFloat() - width / 2) / scaleFactor + biasU
    private fun yToV(y: Int): Float = (y.toFloat() - height / 2) / scaleFactor + biasV

    companion object {
        private val LINE_COLOR = Color.WHITE
    }
}