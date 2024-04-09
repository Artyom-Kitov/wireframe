package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.utils.linear.Vector
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

object ScenePanel : JPanel() {
    private fun readResolve(): Any = ScenePanel

    private val scaleFactor = 300
    private val lines = listOf(
        Vector.of(-1f, -1f, -1f, 1f) to Vector.of(-1f, 1f, -1f, 1f),
        Vector.of(1f, -1f, -1f, 1f) to Vector.of(1f, 1f, -1f, 1f)
    )

    init {
        background = Color.BLACK
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
    }
}