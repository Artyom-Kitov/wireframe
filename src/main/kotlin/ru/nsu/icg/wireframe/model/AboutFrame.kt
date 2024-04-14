package ru.nsu.icg.wireframe.model

import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JDialog
import javax.swing.JLabel

object AboutFrame : JDialog() {
    private fun readResolve(): Any = AboutFrame

    init {
        size = Dimension(300, 240)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        layout = FlowLayout(FlowLayout.CENTER)

        add(JLabel("""
            <html>
                <h1>Wireframe</h1>
                <br/>
                <h3>3D modeling app</h3>
                <br/>
                <h3>Author: Artyom Kitov</h3>
            </html>
        """.trimIndent()))

        title = "About"
        isResizable = false
        setLocationRelativeTo(null)
        isModal = true
        isVisible = false
    }
}