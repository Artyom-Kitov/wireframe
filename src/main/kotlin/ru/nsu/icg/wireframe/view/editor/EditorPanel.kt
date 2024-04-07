package ru.nsu.icg.wireframe.view.editor

import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

@Component
class EditorPanel : JPanel() {
    var splineColorSupplier: () -> Color = { Color.WHITE }
    var nSupplier: () -> Int = { 10 }

    var onSelect: (ControlDot) -> Unit = {  }
    var onUnselect: () -> Unit = {  }

    private val controlDots: MutableList<ControlDot> = mutableListOf()

    private var scaleFactor = 150
    private var selectedDot: ControlDot? = null
    private var dotCounter = 1

    init {
        isDoubleBuffered = true
        background = Color.BLACK
        layout = null
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                selectedDot?.isSelected = false
                selectedDot = null
                onUnselect()
                repaint()
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

    fun onDotDelete() {
        if (selectedDot == null) {
            return
        }
        controlDots.remove(selectedDot)
        remove(selectedDot)
        selectedDot = null
        repaint()
    }

    fun onZoom(factor: Float) {
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
        paintGrid(g)
        paintBSpline(g)
    }

    private fun paintGrid(g: Graphics) {
        g.color = LINE_COLOR
        g.drawLine(width / 2, 0, width / 2, height)
        g.drawLine(0, height / 2, width, height / 2)

        val serifSize = 4
        val sizeX = width / scaleFactor
        val sizeY = height / scaleFactor
        for (i in -sizeX + 1..<sizeX) {
            g.drawLine(width / 2 + i * scaleFactor, height / 2 - serifSize,
                width / 2 + i * scaleFactor, height / 2 + serifSize)
            (g as Graphics2D).drawString(i.toString(), width / 2 + i * scaleFactor + serifSize,
                height / 2 - 2 * serifSize)
        }
        for (i in -sizeY + 1..<sizeY) {
            g.drawLine(width / 2 - serifSize, height / 2 + i * scaleFactor,
                width / 2 + serifSize, height / 2 + i * scaleFactor)
            if (i != 0) {
                (g as Graphics2D).drawString((-i).toString(), width / 2 + 2 * serifSize,
                    height / 2 + i * scaleFactor + 2 * serifSize)
            }
        }
        (g as Graphics2D).drawString("u", width - 20, height / 2 + 20)
        g.drawString("v", width / 2 + 10, 10)

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
        val dots = mutableListOf(controlDots[0], controlDots[0])
        dots.addAll(controlDots)
        dots.add(controlDots.last())
        dots.add(controlDots.last())

        g.color = splineColorSupplier()
        val n = nSupplier()

        var prevU: Float? = null
        var prevV: Float? = null
        for (i in 1..<dots.size - 2) {
            val uArr = arrayOf(dots[i - 1].u, dots[i].u, dots[i + 1].u,
                dots[i + 2].u)
            val vArr = arrayOf(dots[i - 1].v, dots[i].v, dots[i + 1].v,
                dots[i + 2].v)
            val coefsU = coefficients(uArr)
            val coefsV = coefficients(vArr)
            val dt = 1f / n
            for (j in 0..<n) {
                val t = j * dt
                val u = coefsU[0] * t * t * t + coefsU[1] * t * t + coefsU[2] * t + coefsU[3]
                val v = coefsV[0] * t * t * t + coefsV[1] * t * t + coefsV[2] * t + coefsV[3]
                if (prevU == null || prevV == null) {
                    prevU = u
                    prevV = v
                }
                g.drawLine(uToX(prevU), vToY(prevV), uToX(u), vToY(v))
                prevU = u
                prevV = v
            }
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

    private fun uToX(u: Float): Int = (width / 2 + u * scaleFactor).toInt()
    private fun vToY(v: Float): Int = (height / 2 + v * scaleFactor).toInt()
    private fun xToU(x: Int): Float = (x.toFloat() - width / 2) / scaleFactor
    private fun yToV(y: Int): Float = (y.toFloat() - height / 2) / scaleFactor

    companion object {
        private val LINE_COLOR = Color.WHITE
    }
}