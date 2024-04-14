package ru.nsu.icg.wireframe.editor

import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel

class ButtonsPanel(
    onDotAdd: (Float, Float) -> Unit,
    onDotDelete: () -> Unit,
    onClear: () -> Unit,
    onZoom: (Boolean) -> Unit,
    onNormalize: () -> Unit,
    onDotsShown: (Boolean) -> Unit,
    onAutoscale: (Boolean) -> Unit,
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

        val zoomInButton = JButton("Zoom in")
        val zoomOutButton = JButton("Zoom out")
        zoomInButton.addActionListener { onZoom(true) }
        zoomOutButton.addActionListener { onZoom(false) }

        val normalizeButton = JButton("Normalize")
        normalizeButton.addActionListener { onNormalize() }

        val dotsShownBox = JCheckBox("Show points")
        dotsShownBox.isSelected = true
        dotsShownBox.addChangeListener {
            if (!dotsShownBox.isSelected) {
                addButton.isEnabled = false
                deleteButton.isEnabled = false
            } else {
                addButton.isEnabled = true
                deleteButton.isEnabled = true
            }
            onDotsShown(dotsShownBox.isSelected)
        }

        val autoscaleBox = JCheckBox("Autoscale")
        autoscaleBox.isSelected = false
        autoscaleBox.addChangeListener { onAutoscale(autoscaleBox.isSelected) }

        add(applyButton)
        add(addButton)
        add(deleteButton)
        add(clearButton)
        add(zoomInButton)
        add(zoomOutButton)
        add(normalizeButton)
        add(dotsShownBox)
        add(autoscaleBox)
    }
}