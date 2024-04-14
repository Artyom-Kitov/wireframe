package ru.nsu.icg.wireframe.model

import java.awt.Image
import javax.swing.ImageIcon

fun loadImageFromResources(path: String): ImageIcon {
    val imageUrl = object {}.javaClass.getResource(path)
    return ImageIcon(imageUrl)
}

fun loadScaledIconFromResources(path: String, width: Int, height: Int): ImageIcon {
    val icon = loadImageFromResources(path)
    return ImageIcon(icon.image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING))
}