package to.grindelf.naturewandering

import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_HEIGHT
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_NAME
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_WIDTH
import to.grindelf.naturewandering.datamanager.SavesManager
import javax.swing.JFrame
import javax.swing.JOptionPane

object NatureWandering {

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame(WINDOW_NAME)
        val panel: IsometricWorld
        val createWorld: Boolean

        if (SavesManager.ifSavesDirExistAndNotEmpty()) {
            val choice = JOptionPane.showOptionDialog(
                null,
                "Do you want to load an existing world or create a new one?",
                "Choose World",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                arrayOf("Load Existing", "Create New"),
                "Load Existing"
            )
            createWorld = choice == JOptionPane.NO_OPTION
        } else {
            createWorld = true
        }
        panel = IsometricWorld(createWorld)

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
        frame.add(panel)
        frame.isVisible = true
    }
}

// TODO: fix world save file access problem
// TODO: reorganize the assets deeper (classify sounds, textures into sub-cats)
