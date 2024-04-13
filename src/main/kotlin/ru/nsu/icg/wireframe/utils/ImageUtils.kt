package ru.nsu.icg.wireframe.utils

import javax.swing.ImageIcon

fun loadImageFromResources(path: String): ImageIcon {
    val imageUrl = object {}.javaClass.getResource(path)
    return ImageIcon(imageUrl)
}