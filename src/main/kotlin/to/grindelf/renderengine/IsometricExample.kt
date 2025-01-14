package to.grindelf.renderengine

import to.grindelf.renderengine.IsometricWorldConstants.WINDOW_HEIGHT
import to.grindelf.renderengine.IsometricWorldConstants.WINDOW_NAME
import to.grindelf.renderengine.IsometricWorldConstants.WINDOW_WIDTH
import javax.swing.JFrame

object IsometricExample {

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame(WINDOW_NAME)
        val panel = IsometricWorld()

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
        frame.add(panel)
        frame.isVisible = true
    }
}