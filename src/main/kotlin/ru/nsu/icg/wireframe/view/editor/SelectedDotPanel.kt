package ru.nsu.icg.wireframe.view.editor

import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

class SelectedDotPanel : JPanel() {
    var onMove: (ControlDot, Float, Float) -> Unit = { _, _, _ -> run {} }

    private val label = JLabel("Point #")
    private val uField = JTextField()
    private val vField = JTextField()

    private var currentU = 0f
    private var currentV = 0f

    private var dot: ControlDot? = null

    init {
        layout = FlowLayout()

        uField.isEnabled = false
        vField.isEnabled = false

        uField.preferredSize = Dimension(80, uField.preferredSize.height)
        vField.preferredSize = uField.preferredSize

        uField.addActionListener { onEnter() }
        vField.addActionListener { onEnter() }

        add(label)
        add(uField)
        add(vField)
    }

    fun select(dot: ControlDot) {
        label.text = "Point #${dot.index}"

        this.dot = dot
        currentU = dot.u
        currentV = -dot.v

        uField.text = currentU.toString()
        vField.text = currentV.toString()

        uField.isEnabled = true
        vField.isEnabled = true

        repaint()
    }

    fun unselect() {
        label.text = "Point #"
        this.dot = null
        uField.text = ""
        vField.text = ""
        uField.isEnabled = false
        vField.isEnabled = false
        repaint()
    }

    private fun onEnter() {
        try {
            val u = uField.text.toFloat()
            val v = vField.text.toFloat()
            if (dot != null) {
                onMove(dot!!, u, -v)
                currentU = dot!!.u
                currentV = -dot!!.v
            }
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(parent, "Error: invalid coordinates", "Warning",
                JOptionPane.WARNING_MESSAGE)
        }
        uField.text = currentU.toString()
        vField.text = currentV.toString()
    }
}