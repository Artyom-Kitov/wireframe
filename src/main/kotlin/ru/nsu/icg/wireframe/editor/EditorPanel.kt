package ru.nsu.icg.wireframe.editor

import ru.nsu.icg.wireframe.model.linear.Matrix
import ru.nsu.icg.wireframe.model.linear.Vector
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

object EditorPanel : JPanel() {
    private fun readResolve(): Any = EditorPanel

    var splineColorSupplier: () -> Color = { Color.WHITE }
    var nSupplier: () -> Int = { 10 }

    var onSelect: (ControlDot) -> Unit = {  }
    var onUnselect: () -> Unit = {  }

    var autoScale = false
        set(value) {
            field = value
            if (value) rescale()
        }

    private val controlDots: MutableList<ControlDot> = mutableListOf()
    val splineDots = mutableListOf<Vector>()
    val segmentsEnds = mutableListOf<Vector>()

    private const val ZOOM_DELTA = 5f
    private val LINE_COLOR = Color.WHITE
    private val GRID_COLOR = Color.DARK_GRAY
    private const val MIN_SCALE_FACTOR = 0.1f
    private val SPLINE_MATRIX = Matrix.of(
        floatArrayOf(-1f, 3f, -3f, 1f),
        floatArrayOf(3f, -6f, 3f, 0f),
        floatArrayOf(-3f, 0f, 3f, 0f),
        floatArrayOf(1f, 4f, 1f, 0f)
    ) * (1f / 6)

    private var scaleFactor = 150
    private var biasU: Float = 0f
    private var biasV: Float = 0f
    private var selectedDot: ControlDot? = null
    private var dotCounter = 1
    private var dotsShown = true

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
                if (autoScale) rescale()
            }

            override fun mousePressed(e: MouseEvent?) {
                if (e == null || !SwingUtilities.isLeftMouseButton(e)) return
                isPressed = true
                origin = Point(e.x, e.y)
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (e == null) return
                isPressed = false
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                if (autoScale) rescale()
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
                if (e == null) return
                onZoom(e.wheelRotation < 0)
            }
        })
    }

    fun addDot(u: Float, v: Float) {
        if (!dotsShown) {
            return
        }
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
        if (autoScale) rescale()
    }

    fun onClear() {
        controlDots.forEach(::remove)
        controlDots.clear()
        splineDots.clear()
        segmentsEnds.clear()

        biasU = 0f
        biasV = 0f
        scaleFactor = 150

        dotCounter = 1
        selectedDot = null
        onUnselect()
        repaint()
    }

    fun setDotCoordinates(dot: ControlDot, u: Float, v: Float) {
        dot.setLocation(uToX(u), vToY(v))
        dot.u = u
        dot.v = v
        repaint()
    }

    fun normalize() {
        if (controlDots.isEmpty()) return
        var maxCoordinate = abs(controlDots[0].u)
        for (dot in controlDots) {
            if (abs(dot.u) > maxCoordinate) maxCoordinate = abs(dot.u)
            if (abs(dot.v) > maxCoordinate) maxCoordinate = abs(dot.v)
        }
        if (maxCoordinate == 0f) return
        for (dot in controlDots) {
            dot.u /= maxCoordinate
            dot.v /= maxCoordinate
        }
        rescale()
        repaint()
    }

    fun onDotsShown(b: Boolean) {
        dotsShown = b
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

    fun onZoom(zoomIn: Boolean) {
        val sign = if (zoomIn) 1 else -1
        if (scaleFactor + sign * ZOOM_DELTA <= MIN_SCALE_FACTOR) {
            return
        }
        scaleFactor = (scaleFactor + sign * ZOOM_DELTA).toInt()
        if (autoScale) rescale()
        repaint()
    }

    private fun revalidateDots() {
        controlDots.forEach {
            it.setLocation(uToX(it.u), vToY(it.v))
        }
    }

    private fun rescale() {
        if (autoScale && controlDots.size > 1) {
            var uMin = controlDots[0].u
            var uMax = controlDots[0].u
            var vMin = controlDots[0].v
            var vMax = controlDots[0].v
            for (dot in controlDots) {
                if (dot.u < uMin) uMin = dot.u
                if (dot.u > uMax) uMax = dot.u
                if (dot.v < vMin) vMin = dot.v
                if (dot.v > vMax) vMax = dot.v
            }
            if (xToU(0) > uMin || xToU(width) < uMax || yToV(0) > vMin || yToV(height) < vMax) {
                val factor = 1.1f
                fitTo(uMin * factor, uMax * factor, vMin * factor, vMax * factor)
                return
            }
            val minBox = 100
            if (uToX(uMax) - uToX(uMin) < minBox || vToY((vMax)) - vToY(vMin) < minBox) {
                val factor = 1.1f
                fitTo(uMin * factor, uMax * factor, vMin * factor, vMax * factor)
            }
        }
    }

    private fun fitTo(uMin: Float, uMax: Float, vMin: Float, vMax: Float) {
        val newFactor = min((width / (uMax - uMin)).toInt(), (height / (vMax - vMin)).toInt())
        if (abs(newFactor) < MIN_SCALE_FACTOR) return
        scaleFactor = newFactor
        biasU = (uMax + uMin) / 2
        biasV = (vMax + vMin) / 2
        repaint()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return
        revalidateDots()
        paintGrid(g)
        paintBSpline(g)
    }

    private fun paintGrid(g: Graphics) {
        val biasX = (biasU * scaleFactor).toInt()
        val biasY = (biasV * scaleFactor).toInt()

        val serifSize = 4
        val sizeX = width / scaleFactor + abs(biasX)
        val sizeY = height / scaleFactor + abs(biasY)
        for (i in -sizeX + 1..<sizeX) {
            g.color = GRID_COLOR
            g.drawLine(width / 2 + i * scaleFactor - biasX, 0,
                width / 2 + i * scaleFactor - biasX, height)
            g.color = LINE_COLOR
            (g as Graphics2D).drawString(i.toString(), width / 2 + i * scaleFactor + serifSize - biasX,
                height / 2 - 2 * serifSize - biasY)
        }
        for (i in -sizeY + 1..<sizeY) {
            g.color = GRID_COLOR
            g.drawLine(0, height / 2 + i * scaleFactor - biasY,
                width, height / 2 + i * scaleFactor - biasY)
            if (i != 0) {
                g.color = LINE_COLOR
                (g as Graphics2D).drawString((-i).toString(), width / 2 + 2 * serifSize - biasX,
                    height / 2 + i * scaleFactor + 2 * serifSize - biasY)
            }
        }
        g.color = LINE_COLOR
        g.drawLine(0, height / 2 - biasY, width, height / 2 - biasY)
        g.drawLine(width / 2 - biasX, 0, width / 2 - biasX, height)
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
        segmentsEnds.clear()
        val last = dots.lastIndex - 2
        for (i in 1..last) {
            val pU = SPLINE_MATRIX * Vector.of(dots[i - 1].u, dots[i].u, dots[i + 1].u, dots[i + 2].u)
            val pV = SPLINE_MATRIX * Vector.of(dots[i - 1].v, dots[i].v, dots[i + 1].v, dots[i + 2].v)
            val dt = 1f / n
            for (j in 0..< if (i == last) n + 1 else n) {
                val t = j * dt
                val vectorT = Vector.of(t * t * t, t * t, t, 1f)
                val dot = Vector.of(pU * vectorT, pV * vectorT)
                splineDots.add(dot)
                if (j == 0 || j == n) {
                    segmentsEnds.add(dot)
                }
            }
        }
        for (i in 0..<splineDots.lastIndex) {
            val prevU = splineDots[i][0]
            val prevV = splineDots[i][1]
            val u = splineDots[i + 1][0]
            val v = splineDots[i + 1][1]
            g.drawLine(uToX(prevU), vToY(prevV), uToX(u), vToY(v))
        }
    }

    private fun onDotDragged(dot: ControlDot) {
        val newX = dot.location.x
        val newY = dot.location.y
        dot.u = xToU(newX)
        dot.v = yToV(newY)
        onSelect(dot)
        if (autoScale) rescale()
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
}