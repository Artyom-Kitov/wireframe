package ru.nsu.icg.wireframe.editor

import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel

class ButtonsPanel(
    onDotAdd: (Float, Float) -> Unit,
    onDotDelete: () -> Unit,
    onClear: () -> Unit,
    onZoom: (Float) -> Unit,
    onDotsShown: (Boolean) -> Unit,
    onApply: () -> Unit,
) : JPanel() {
    init {
        val applyButton = JButton("Apply")
        applyButton.addActionListener { onApply() }

        val addButton = JButton("Add point")
        addButton.addActionListener { onDotAdd(0f, 0f) }

        val deleteButton = JButton("Delete point")
        deleteButton.addActionListener { onDotDelete() }

        val clearButton = JButton("Clear")
        clearButton.addActionListener { onClear() }

        val onZoomInButton = JButton("Zoom in")
        val onZoomOutButton = JButton("Zoom out")
        onZoomInButton.addActionListener { onZoom(1.2f) }
        onZoomOutButton.addActionListener { onZoom(0.8333333f) }

        val onDotsShownBox = JCheckBox("Show points")
        onDotsShownBox.isSelected = true
        onDotsShownBox.addChangeListener {
            if (!onDotsShownBox.isSelected) {
                addButton.isEnabled = false
                deleteButton.isEnabled = false
            } else {
                addButton.isEnabled = true
                deleteButton.isEnabled = true
            }
            onDotsShown(onDotsShownBox.isSelected)
        }

        add(applyButton)
        add(addButton)
        add(deleteButton)
        add(clearButton)
        add(onZoomInButton)
        add(onZoomOutButton)
        add(onDotsShownBox)
    }
}