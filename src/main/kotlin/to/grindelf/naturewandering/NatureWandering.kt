package to.grindelf.naturewandering

import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_HEIGHT
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_NAME
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_WIDTH
import to.grindelf.naturewandering.datamanager.SavesManager
import java.awt.Color
import java.awt.Font
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import kotlin.system.exitProcess

object NatureWandering {

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame(WINDOW_NAME)
        var panel: IsometricWorld // Declaring `panel` as `var` so it can be reassigned
        var createWorld = false
        var worldName: String? = "world"

        // Set fullscreen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH)
        frame.isUndecorated = true // Remove window decorations for a fullscreen game feel

        // Create a main menu panel
        val mainMenuPanel = JPanel()
        mainMenuPanel.background = Color(120, 170, 120) // Dark background
        mainMenuPanel.layout = null  // Absolute positioning for a more customizable layout

        // Add a "Create New World" button
        val createWorldButton = JButton("Create New World")
        createWorldButton.font = Font("Arial", Font.PLAIN, 24)
        createWorldButton.bounds = Rectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 3, 300, 50)
        createWorldButton.addActionListener {
            createWorld = true
            worldName = showWorldNameDialog()
            if (!worldName.isNullOrEmpty()) {
                frame.contentPane.removeAll()  // Remove the current menu
                panel = IsometricWorld(createWorld, worldName!!)
                frame.add(panel)
                frame.revalidate()
                frame.repaint()
            } else {
                // If the user cancels, just return to the main menu
                frame.contentPane.removeAll()
                frame.contentPane.add(mainMenuPanel)
                frame.revalidate()
                frame.repaint()
            }
        }
        mainMenuPanel.add(createWorldButton)

        // Add a "Load Existing World" button if there are saved worlds
        val loadWorldButton = JButton("Load Existing World")
        loadWorldButton.font = Font("Arial", Font.PLAIN, 24)
        loadWorldButton.bounds = Rectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 3 + 50, 300, 50)
        loadWorldButton.addActionListener {
            if (SavesManager.ifSavesDirExistAndNotEmpty()) {
                worldName = showLoadWorldDialog()
                if (worldName!!.isNotEmpty()) {
                    createWorld = false
                    frame.contentPane.removeAll()  // Remove the current menu
                    panel = IsometricWorld(createWorld, worldName!!)
                    frame.add(panel)
                    frame.revalidate()
                    frame.repaint()
                }
            } else {
                JOptionPane.showMessageDialog(null, "No saved worlds available to load!")
            }
        }
        mainMenuPanel.add(loadWorldButton)

        // Add an "Exit Game" button with confirmation dialog
        val exitButton = JButton("Exit Game")
        exitButton.font = Font("Arial", Font.PLAIN, 24)
        exitButton.bounds = Rectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 3 + 100, 300, 50)
        exitButton.addActionListener {
            val option = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit?",
                "Exit Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            )
            if (option == JOptionPane.YES_OPTION) {
                exitProcess(0)
            }
        }
        mainMenuPanel.add(exitButton)

        // Display the menu panel
        frame.contentPane.add(mainMenuPanel)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)
        frame.isVisible = true
    }

    private fun showWorldNameDialog(): String? {
        val worldName = JOptionPane.showInputDialog("Enter new world name:")
        return worldName  // Will return null if the user presses "Cancel"
    }

    private fun showLoadWorldDialog(): String {
        val savesDir = SavesManager.getSavesDirectory()
        val worldFiles = savesDir.listFiles()?.filter { it.extension == "json" }?.map { it.nameWithoutExtension }
        if (worldFiles.isNullOrEmpty()) {
            JOptionPane.showMessageDialog(null, "No saved worlds available to load!")
            return ""
        }

        val worldName = JOptionPane.showInputDialog(
            null,
            "Select a world to load:",
            "Load Existing World",
            JOptionPane.QUESTION_MESSAGE,
            null,
            worldFiles.toTypedArray(),
            worldFiles.firstOrNull()
        ) as? String

        return worldName ?: ""
    }
}
