package ru.nsu.icg.wireframe.editor

import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel

class ButtonsPanel(
    onDotAdd: (Float, Float) -> Unit,
    onDotDelete: () -> Unit,
    onZoom: (Float) -> Unit,
    onDotsShown: (Boolean) -> Unit,
    onApply: () -> Unit,
) : JPanel() {
    init {
        val addButton = JButton("Add point")
        addButton.addActionListener { onDotAdd(0f, 0f) }

        val deleteButton = JButton("Delete point")
        deleteButton.addActionListener { onDotDelete() }

        val onZoomInButton = JButton("Zoom in")
        val onZoomOutButton = JButton("Zoom out")
        onZoomInButton.addActionListener { onZoom(1.2f) }
        onZoomOutButton.addActionListener { onZoom(0.8333333f) }

        val onDotsShownBox = JCheckBox("Show points")
        onDotsShownBox.isSelected = true
        onDotsShownBox.addChangeListener {
            onDotsShown(onDotsShownBox.isSelected)
        }

        add(addButton)
        add(deleteButton)
        add(onZoomInButton)
        add(onZoomOutButton)
        add(onDotsShownBox)
    }
}