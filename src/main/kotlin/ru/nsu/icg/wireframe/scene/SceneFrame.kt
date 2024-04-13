package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.editor.EditorFrame
import ru.nsu.icg.wireframe.utils.AboutFrame
import ru.nsu.icg.wireframe.utils.FileManager
import ru.nsu.icg.wireframe.utils.loadImageFromResources
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.math.PI

object SceneFrame : JFrame("Wireframe") {
    private fun readResolve(): Any = SceneFrame

    private val MINIMUM_SIZE = Dimension(640, 480)

    init {
        extendedState = MAXIMIZED_BOTH
        minimumSize = MINIMUM_SIZE
        defaultCloseOperation = EXIT_ON_CLOSE

        iconImage = loadImageFromResources("/icons/logo.png").image

        setupMenu()

        ScenePanel.preferredSize = Dimension(preferredSize)
        this.add(ScenePanel)
        isFocusable = true

        this.pack()
        isVisible = true
    }

    private fun setupMenu() {
        val menuBar = JMenuBar()

        val file = JMenu("File")
        val edit = JMenuItem("Editor")
        val reset = JMenuItem("Reset")
        val about = JMenuItem("About")

        val fileOpen = JMenuItem("Open")
        val fileSave = JMenuItem("Save")
        fileOpen.addActionListener {
            val figure = FileManager.open()
            if (figure != null) ScenePanel.figure = figure
        }
        fileSave.addActionListener { FileManager.save(ScenePanel.figure) }
        file.add(fileOpen)
        file.add(fileSave)

        reset.addActionListener { ScenePanel.reset() }
        edit.addActionListener { EditorFrame.isVisible = true }
        about.addActionListener { AboutFrame.isVisible = true }

        menuBar.add(file)
        menuBar.add(edit)
        menuBar.add(reset)
        menuBar.add(about)
        this.jMenuBar = menuBar
    }
}