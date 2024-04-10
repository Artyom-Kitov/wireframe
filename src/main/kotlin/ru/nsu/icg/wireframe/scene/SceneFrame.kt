package ru.nsu.icg.wireframe.scene

import ru.nsu.icg.wireframe.editor.EditorFrame
import ru.nsu.icg.wireframe.utils.AboutFrame
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.math.PI

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
        isFocusable = true

        this.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                val angle = PI.toFloat() / 24
                when (e?.keyCode) {
                    KeyEvent.VK_Q -> ScenePanel.rotate(angle, 0f, 0f)
                    KeyEvent.VK_E -> ScenePanel.rotate(-angle, 0f, -0f)

                    KeyEvent.VK_A -> ScenePanel.rotate(0f, angle, 0f)
                    KeyEvent.VK_D -> ScenePanel.rotate(0f, -angle, 0f)

                    KeyEvent.VK_W -> ScenePanel.rotate(0f, 0f, angle)
                    KeyEvent.VK_S -> ScenePanel.rotate(0f, 0f, -angle)
                }
            }
        })

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