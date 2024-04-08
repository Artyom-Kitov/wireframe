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

        this.add(JLabel(parameter.name))
        this.add(spinner)

        preferredSize = SIZE
        layout = FlowLayout()
    }

    companion object {
        private val SIZE = Dimension(100, 30)
    }
}
