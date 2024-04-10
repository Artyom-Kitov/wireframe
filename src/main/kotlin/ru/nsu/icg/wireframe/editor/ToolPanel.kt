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

    private fun findProperty(name: String): Int {
        for (panel in parametersPanels) {
            if (panel.parameterName == name) {
                return panel.value
            }
        }
        throw IllegalStateException("property with name $name not found")
    }

    val colorSupplier: () -> Color = {
        val r = findProperty("R")
        val g = findProperty("G")
        val b = findProperty("B")
        Color(r, g, b)
    }
    val nSupplier: () -> Int = { findProperty("Line smoothness") }
    val mSupplier: () -> Int = { findProperty("Splines") }
    val m1Supplier: () -> Int = { findProperty("Circle smoothness") }
}