package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.editor.EditorFrame
import ru.nsu.icg.wireframe.model.AboutFrame
import ru.nsu.icg.wireframe.model.FileManager
import ru.nsu.icg.wireframe.model.loadImageFromResources
import ru.nsu.icg.wireframe.model.loadScaledIconFromResources
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.system.exitProcess

object SceneFrame : JFrame("Wireframe") {
    private fun readResolve(): Any = SceneFrame

    private val MINIMUM_SIZE = Dimension(640, 480)

    init {
        extendedState = MAXIMIZED_BOTH
        minimumSize = MINIMUM_SIZE
        defaultCloseOperation = EXIT_ON_CLOSE

        iconImage = loadImageFromResources("/icons/logo.png").image

        setupMenu()
        setupToolBar()

        ScenePanel.preferredSize = Dimension(preferredSize)
        this.add(ScenePanel)
        isFocusable = true

        this.pack()
        isVisible = true
    }

    private fun openScene() {
        val scene = FileManager.open()
        if (scene != null) ScenePanel.scene = scene
    }

    private fun setupMenu() {
        val menuBar = JMenuBar()

        val file = JMenu("File")
        val edit = JMenuItem("Editor")
        val reset = JMenuItem("Reset")
        val about = JMenuItem("About")

        val fileOpen = JMenuItem("Open")
        val fileSave = JMenuItem("Save")
        fileOpen.addActionListener { openScene() }
        fileSave.addActionListener { FileManager.save(ScenePanel.scene) }
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

    private fun setupToolBar() {
        val toolBar = JToolBar()
        toolBar.preferredSize = Dimension(32, 32)
        toolBar.isFloatable = false

        val buttonWidth = 25
        val buttonHeight = 25

        val open = JButton()
        open.icon = loadScaledIconFromResources("/icons/open.png", buttonWidth, buttonHeight)
        open.toolTipText = "Open scene"
        open.addActionListener { openScene() }
        toolBar.add(open)

        val save = JButton()
        save.icon = loadScaledIconFromResources("/icons/save.png", buttonWidth, buttonHeight)
        save.toolTipText = "Save scene"
        save.addActionListener { FileManager.save(ScenePanel.scene) }
        toolBar.add(save)

        toolBar.addSeparator()

        val openEditor = JButton()
        openEditor.icon = loadScaledIconFromResources("/icons/open-editor.png", buttonWidth, buttonHeight)
        openEditor.toolTipText = "Open spline editor"
        openEditor.addActionListener { EditorFrame.isVisible = true }
        toolBar.add(openEditor)

        val reset = JButton()
        reset.icon = loadScaledIconFromResources("/icons/reset.png", buttonWidth, buttonHeight)
        reset.toolTipText = "Reset scene rotation angles"
        reset.addActionListener { ScenePanel.reset() }
        toolBar.add(reset)

        toolBar.addSeparator()

        val about = JButton()
        about.icon = loadScaledIconFromResources("/icons/about.png", buttonWidth, buttonHeight)
        about.toolTipText = "About"
        about.addActionListener { AboutFrame.isVisible = true }
        toolBar.add(about)

        val exit = JButton()
        exit.icon = loadScaledIconFromResources("/icons/exit.png", buttonWidth, buttonHeight)
        exit.toolTipText = "Exit"
        exit.addActionListener { exitProcess(0) }
        toolBar.add(exit)

        add(toolBar, BorderLayout.NORTH)
    }
}