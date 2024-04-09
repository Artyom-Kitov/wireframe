package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.editor.EditorFrame
import ru.nsu.icg.wireframe.utils.AboutFrame
import java.awt.Dimension
import javax.swing.*

object SceneFrame : JFrame("Wireframe") {
    private fun readResolve(): Any = SceneFrame

    private val DEFAULT_SIZE = Dimension(1280, 720)
    private val MINIMUM_SIZE = Dimension(640, 480)

    init {
        preferredSize = DEFAULT_SIZE
        minimumSize = MINIMUM_SIZE
        defaultCloseOperation = EXIT_ON_CLOSE

        setupMenu()

        ScenePanel.preferredSize = Dimension(preferredSize)
        this.add(ScenePanel)

        this.pack()
        isVisible = true
    }

    private fun setupMenu() {
        val menuBar = JMenuBar()

        val file = JMenu("File")
        val edit = JMenuItem("Editor")
        val about = JMenuItem("About")

        val fileOpen = JMenuItem("Open")
        val fileSave = JMenuItem("Save")
        file.add(fileOpen)
        file.add(fileSave)

        edit.addActionListener { EditorFrame.isVisible = true }
        about.addActionListener { AboutFrame.isVisible = true }

        menuBar.add(file)
        menuBar.add(edit)
        menuBar.add(about)
        this.jMenuBar = menuBar
    }
}