package ru.nsu.icg.wireframe

import com.formdev.flatlaf.FlatDarkLaf
import ru.nsu.icg.wireframe.scene.SceneFrame
import javax.swing.UIManager

fun main() {
    try {
        UIManager.setLookAndFeel(FlatDarkLaf())
    } catch (e: ClassNotFoundException) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    }
    SceneFrame.isVisible = true
}