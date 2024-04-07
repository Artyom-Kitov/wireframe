package ru.nsu.icg.wireframe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import javax.swing.UIManager

@SpringBootApplication
class WireframeApplication

fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val builder = SpringApplicationBuilder(WireframeApplication::class.java)
    builder.headless(false)
    builder.run(*args)
}
