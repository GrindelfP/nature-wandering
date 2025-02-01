package to.grindelf.naturewandering

import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_HEIGHT
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_NAME
import to.grindelf.naturewandering.IsometricWorldConstants.WINDOW_WIDTH
import to.grindelf.naturewandering.datamanager.saves.SavesManager
import java.awt.Color
import java.awt.Font
import java.awt.Toolkit
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

        // Get screen size from Toolkit
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenWidth = screenSize.width
        val screenHeight = screenSize.height

        // Center calculation
        val buttonWidth = 300
        val buttonHeight = 50
        val verticalSpacing = 60 // Space between buttons

        // Add a "Create New World" button
        val createWorldButton = JButton("Create New World")
        createWorldButton.font = Font("Arial", Font.PLAIN, 24)
        createWorldButton.setBounds(
            (screenWidth - buttonWidth) / 2, // Center horizontally
            (screenHeight - 2 * verticalSpacing - buttonHeight) / 3, // Center vertically
            buttonWidth,
            buttonHeight
        )
        createWorldButton.addActionListener {
            createWorld = true
            worldName = showWorldNameDialog()
            if (!worldName.isNullOrEmpty()) {
                frame.contentPane.removeAll()  // Remove the current menu
                panel = IsometricWorld(createWorld, worldName!!, frame, mainMenuPanel)
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
        loadWorldButton.setBounds(
            (screenWidth - buttonWidth) / 2, // Center horizontally
            (screenHeight - verticalSpacing - buttonHeight) / 3 + verticalSpacing, // Center vertically with spacing
            buttonWidth,
            buttonHeight
        )
        loadWorldButton.addActionListener {
            if (SavesManager.ifSavesDirExistAndNotEmpty()) {
                worldName = showLoadWorldDialog()
                if (worldName!!.isNotEmpty()) {
                    createWorld = false
                    frame.contentPane.removeAll()  // Remove the current menu
                    panel = IsometricWorld(createWorld, worldName!!, frame, mainMenuPanel)
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
        exitButton.setBounds(
            (screenWidth - buttonWidth) / 2, // Center horizontally
            (screenHeight - buttonHeight) / 3 + 2 * verticalSpacing, // Center vertically with spacing
            buttonWidth,
            buttonHeight
        )
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
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT)  // You may keep your preferred window size here
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
