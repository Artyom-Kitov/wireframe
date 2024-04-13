package ru.nsu.icg.wireframe.utils

import ru.nsu.icg.wireframe.utils.linear.Vector

data class Line(
    private val dots: MutableList<Vector> = mutableListOf()
) : MutableList<Vector> by dots