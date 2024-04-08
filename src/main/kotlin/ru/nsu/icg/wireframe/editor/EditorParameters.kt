package ru.nsu.icg.wireframe.editor

import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

data class Parameter(
    val name: String,
    val min: Number,
    val max: Number,
    val initial: Number,
    val step: Number,
)

object ParametersReader {
    fun parameters(): List<Parameter> {
        val stream = javaClass.getResourceAsStream(PATH)
            ?: throw IllegalStateException("error accessing $PATH")
        val parametersList = JSONArray(BufferedReader(InputStreamReader(stream)).readText())
        val result: MutableList<Parameter> = mutableListOf()
        for (i in 0..<parametersList.length()) {
            val parameter = parametersList.getJSONObject(i)
            result.add(
                Parameter(
                    name = parameter.getString("name"),
                    min = parameter.getNumber("min"),
                    max = parameter.getNumber("max"),
                    initial = parameter.getNumber("initial"),
                    step = parameter.getNumber("step")
                )
            )
        }
        return result
    }

    private const val PATH = "/parameters.json"
}