package to.grindelf.renderengine

import javax.swing.JFrame

object IsometricExample {

    @JvmStatic
    fun main(args: Array<String>) {
        // Создание и настройка окна
        val frame = JFrame("2D Isometric World")
        val panel = IsometricWorld()

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(1600, 1200)
        frame.add(panel)
        frame.isVisible = true
    }
}