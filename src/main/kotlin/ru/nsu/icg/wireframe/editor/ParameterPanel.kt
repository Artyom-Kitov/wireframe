package ru.nsu.icg.wireframe.editor

import java.awt.*
import javax.swing.*

class ParameterPanel(parameter: Parameter) : JPanel() {
    val value
        get() = spinner.value as Int
    val parameterName = parameter.name
    var onChange: () -> Unit = {}

    private val spinner = JSpinner()

    init {
        spinner.preferredSize = Dimension(SIZE.width - 30, spinner.preferredSize.height)
        spinner.model = SpinnerNumberModel(parameter.initial, parameter.min.toInt(),
            parameter.max.toInt(), parameter.step)
        spinner.addChangeListener {
            onChange()
        }

        val labelPanel = JPanel()
        val label = JLabel(parameter.name)
        labelPanel.preferredSize = Dimension(SIZE.width, (label.preferredSize.height * 1.5).toInt())
        labelPanel.add(label)
        this.add(labelPanel, BorderLayout.NORTH)
        this.add(spinner, BorderLayout.SOUTH)

        preferredSize = SIZE
    }

    companion object {
        private val SIZE = Dimension(100, 60)
    }
}
