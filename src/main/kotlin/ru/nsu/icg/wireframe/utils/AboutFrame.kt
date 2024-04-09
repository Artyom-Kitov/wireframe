package ru.nsu.icg.wireframe.utils

import java.awt.Dimension
import javax.swing.JDialog

object AboutFrame : JDialog() {
    init {
        size = Dimension(300, 300)
        defaultCloseOperation = DISPOSE_ON_CLOSE

        title = "About"
        isResizable = false
        setLocationRelativeTo(null)
        isModal = true
        isVisible = false
    }
}