package ru.nsu.icg.wireframe.model

import com.google.gson.JsonParseException
import ru.nsu.icg.wireframe.scene.ScenePanel
import java.io.File
import java.io.IOException
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

object FileManager : JFileChooser() {
    private fun readResolve(): Any = FileManager

    private const val DESCRIPTION = "Format: .json"
    private const val SUPPORTED_FORMAT = "json"
    private const val SAVE_TITLE = "Save as"
    private const val OPEN_TITLE = "Open"

    init {
        fileFilter = FileNameExtensionFilter(DESCRIPTION, SUPPORTED_FORMAT)
        isMultiSelectionEnabled = false
        dragEnabled = true
    }

    fun open(): Figure.SerializedFigure? {
        dialogTitle = OPEN_TITLE
        dialogType = OPEN_DIALOG

        val result = showOpenDialog(null)
        when (result) {
            APPROVE_OPTION -> return try {
                Figure.readFrom(selectedFile)
            } catch (e: IOException) {
                null
            } catch (e: JsonParseException) {
                JOptionPane.showMessageDialog(this,
                    "Invalid scene file",
                    "Error", JOptionPane.ERROR_MESSAGE)
                return null
            }
            ERROR_OPTION -> {
                JOptionPane.showMessageDialog(this,
                    "Something went wrong, please try again",
                    "Error", JOptionPane.ERROR_MESSAGE)
                return null
            }
        }
        return null
    }

    fun save(figure: Figure) {
        dialogTitle = SAVE_TITLE
        dialogType = SAVE_DIALOG
        val result = showSaveDialog(null)
        if (result == APPROVE_OPTION) {
            if (!selectedFile.absolutePath.endsWith(SUPPORTED_FORMAT)) {
                selectedFile = File("${selectedFile.absolutePath}.$SUPPORTED_FORMAT")
            }
            try {
                figure.writeTo(selectedFile, ScenePanel.rotationMatrix, ScenePanel.screenDistance,
                    ScenePanel.color)
            } catch (e: IOException) {
                JOptionPane.showMessageDialog(this,
                    "Couldn't save to " + selectedFile.name,
                    "Error", JOptionPane.ERROR_MESSAGE)
            }
        }
    }
}