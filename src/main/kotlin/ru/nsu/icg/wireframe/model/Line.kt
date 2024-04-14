package ru.nsu.icg.wireframe.model

import ru.nsu.icg.wireframe.model.linear.Vector

data class Line(
    private val dots: MutableList<Vector> = mutableListOf()
) : MutableList<Vector> by dots