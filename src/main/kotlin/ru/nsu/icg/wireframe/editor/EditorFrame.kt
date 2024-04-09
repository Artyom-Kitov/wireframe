package ru.nsu.icg.wireframe.editor

import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame
import javax.swing.SpringLayout
import javax.swing.WindowConstants

object EditorFrame : JFrame("Wireframe spline editor") {
    private fun readResolve(): Any = EditorFrame

    private val DEFAULT_SIZE = Dimension(1280, 720)
    private val MINIMUM_SIZE = Dimension(640, 480)

    init {
        preferredSize = DEFAULT_SIZE
        minimumSize = MINIMUM_SIZE
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

        val selectedDotPanel = SelectedDotPanel()

        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                EditorPanel.size = Dimension(size.width, size.height * 8 / 10)
                EditorPanel.preferredSize = Dimension(size.width, size.height * 8 / 10)
                ToolPanel.size = Dimension(size.width * 3 / 4, size.height / 10)
                ToolPanel.preferredSize = Dimension(size.width * 3 / 4, size.height / 10)
                revalidate()
                repaint()
            }
        })

        EditorPanel.preferredSize = Dimension(preferredSize.width, preferredSize.height * 8 / 10)
        ToolPanel.preferredSize = Dimension(preferredSize.width * 3 / 4, preferredSize.height / 10)

        EditorPanel.splineColorSupplier = ToolPanel.colorSupplier
        EditorPanel.nSupplier = ToolPanel.nSupplier
        EditorPanel.onSelect = selectedDotPanel::select
        EditorPanel.onUnselect = selectedDotPanel::unselect

        selectedDotPanel.onMove = EditorPanel::setDotCoordinates

        ToolPanel.onChange = EditorPanel::repaint

        val onDelete = {
            EditorPanel.onDotDelete()
            selectedDotPanel.unselect()
        }

        val buttonPanel = ButtonsPanel(
            onDotAdd = EditorPanel::addDot,
            onDotDelete = onDelete,
            onApply = {  },
            onDotsShown = EditorPanel::onDotsShown,
            onClear = EditorPanel::onClear,
            onZoom = EditorPanel::onZoom,
        )
        buttonPanel.preferredSize = ToolPanel.preferredSize

        val springLayout = SpringLayout()
        springLayout.putConstraint(SpringLayout.NORTH, contentPane, 0, SpringLayout.NORTH, EditorPanel)
        springLayout.putConstraint(SpringLayout.NORTH, ToolPanel, 0, SpringLayout.SOUTH, EditorPanel)
        springLayout.putConstraint(SpringLayout.WEST, selectedDotPanel, 0, SpringLayout.EAST, ToolPanel)
        springLayout.putConstraint(SpringLayout.NORTH, selectedDotPanel, 0, SpringLayout.SOUTH, EditorPanel)
        springLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 0, SpringLayout.SOUTH, ToolPanel)
        layout = springLayout

        this.add(EditorPanel)
        this.add(ToolPanel)
        this.add(selectedDotPanel)
        this.add(buttonPanel)

        this.pack()
        isVisible = false
    }
}