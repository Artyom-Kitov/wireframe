package ru.nsu.icg.wireframe

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import javax.swing.UIManager

@SpringBootApplication
class WireframeApplication

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(FlatDarkLaf())
    } catch (e: ClassNotFoundException) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    }
    val builder = SpringApplicationBuilder(WireframeApplication::class.java)
    builder.headless(false)
    builder.run(*args)
}
