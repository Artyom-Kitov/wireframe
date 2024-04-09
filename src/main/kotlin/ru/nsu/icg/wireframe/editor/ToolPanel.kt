package ru.nsu.icg.wireframe.editor

import java.awt.Color
import javax.swing.JPanel

object ToolPanel : JPanel() {
    private fun readResolve(): Any = ToolPanel

    private val parametersPanels: MutableList<ParameterPanel> = mutableListOf()
    var onChange: () -> Unit = {}
        set(value) = parametersPanels.forEach { it.onChange = value }

    init {
        ParametersReader.parameters().forEach {
            val panel = ParameterPanel(it)
            panel.onChange = onChange
            parametersPanels.add(panel)
            this.add(panel)
        }
    }

    val colorSupplier: () -> Color = {
        var r = 0
        var g = 0
        var b = 0
        for (panel in parametersPanels) {
            when (panel.parameterName) {
                "R" -> r = panel.value
                "G" -> g = panel.value
                "B" -> b = panel.value
                else -> {}
            }
        }
        Color(r, g, b)
    }
    val nSupplier: () -> Int = {
        var value = 0
        for (panel in parametersPanels) {
            if (panel.parameterName == "Smoothness") {
                value = panel.value
            }
        }
        value
    }
}