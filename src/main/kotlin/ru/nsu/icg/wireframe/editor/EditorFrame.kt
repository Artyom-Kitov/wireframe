package ru.nsu.icg.wireframe.editor

import org.springframework.stereotype.Component
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.SpringLayout
import javax.swing.WindowConstants

@Component
class EditorFrame(
    editorPanel: EditorPanel,
    toolPanel: ToolPanel
) : JFrame("Wireframe spline editor") {
    init {
        preferredSize = DEFAULT_SIZE
        minimumSize = MINIMUM_SIZE
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        val selectedDotPanel = SelectedDotPanel()

        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                editorPanel.size = Dimension(size.width, size.height * 8 / 10)
                editorPanel.preferredSize = Dimension(size.width, size.height * 8 / 10)
                toolPanel.size = Dimension(size.width / 2, size.height / 10)
                toolPanel.preferredSize = Dimension(size.width / 2, size.height / 10)
                revalidate()
                repaint()
            }
        })

        editorPanel.preferredSize = Dimension(preferredSize.width, preferredSize.height * 8 / 10)
        toolPanel.preferredSize = Dimension(preferredSize.width / 2, preferredSize.height / 10)

        editorPanel.splineColorSupplier = toolPanel.colorSupplier
        editorPanel.nSupplier = toolPanel.nSupplier
        editorPanel.onSelect = selectedDotPanel::select
        editorPanel.onUnselect = selectedDotPanel::unselect

        selectedDotPanel.onMove = editorPanel::setDotCoordinates

        toolPanel.onChange = editorPanel::repaint

        val onDelete = {
            editorPanel.onDotDelete()
            selectedDotPanel.unselect()
        }

        val buttonPanel = ButtonsPanel(
            onDotAdd = editorPanel::addDot,
            onDotDelete = onDelete,
            onApply = {  },
            onDotsShown = editorPanel::onDotsShown,
            onZoom = editorPanel::onZoom,
        )
        buttonPanel.preferredSize = toolPanel.preferredSize

        val springLayout = SpringLayout()
        springLayout.putConstraint(SpringLayout.NORTH, contentPane, 0, SpringLayout.NORTH, editorPanel)
        springLayout.putConstraint(SpringLayout.NORTH, toolPanel, 0, SpringLayout.SOUTH, editorPanel)
        springLayout.putConstraint(SpringLayout.WEST, selectedDotPanel, 0, SpringLayout.EAST, toolPanel)
        springLayout.putConstraint(SpringLayout.NORTH, selectedDotPanel, 0, SpringLayout.SOUTH, editorPanel)
        springLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 0, SpringLayout.SOUTH, toolPanel)
        layout = springLayout

        this.add(editorPanel)
        this.add(toolPanel)
        this.add(selectedDotPanel)
        this.add(buttonPanel)

        this.pack()
        isVisible = true
    }

    companion object {
        private val DEFAULT_SIZE = Dimension(1280, 720)
        private val MINIMUM_SIZE = Dimension(640, 480)
    }
}