package to.grindelf.naturewandering.ui

import to.grindelf.naturewandering.IsometricWorld
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField

class Menu(private val frame: JFrame) {

    fun showInitialMenu() {
        val hasWorlds = File("worlds").listFiles()?.any { it.extension == "json" } == true
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val createButton = JButton("Create New World")
        val loadButton = JButton("Load Existing World").apply {
            isEnabled = hasWorlds
        }

        createButton.addActionListener {
            frame.contentPane.removeAll()
            showCreateWorldMenu()
        }

        loadButton.addActionListener {
            frame.contentPane.removeAll()
            showLoadWorldMenu()
        }

        panel.add(createButton)
        panel.add(loadButton)

        frame.contentPane.add(panel)
        frame.validate()
        frame.repaint()
    }

    private fun showCreateWorldMenu() {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val worldNameField = JTextField(20)
        val createButton = JButton("Create World")

        createButton.addActionListener {
            val worldName = worldNameField.text.trim()
            if (worldName.isNotEmpty()) {
                val worldFile = File("worlds/$worldName.json")
                if (!worldFile.exists()) {
                    IsometricWorld(true, worldName).also { saveWorld(it, worldFile) }
                    startGame(worldName)
                } else {
                    JOptionPane.showMessageDialog(frame, "World already exists!")
                }
            }
        }

        panel.add(JLabel("Enter World Name:"))
        panel.add(worldNameField)
        panel.add(createButton)

        frame.contentPane.add(panel)
        frame.validate()
        frame.repaint()
    }

    private fun showLoadWorldMenu() {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val worlds = File("worlds").listFiles()?.filter { it.extension == "json" } ?: emptyList()
        val worldList = JList(worlds.map { it.nameWithoutExtension }.toTypedArray())
        val loadButton = JButton("Load World")

        loadButton.addActionListener {
            val selectedWorld = worldList.selectedValue
            if (selectedWorld != null) {
                startGame(selectedWorld)
            }
        }

        panel.add(JLabel("Select a World:"))
        panel.add(JScrollPane(worldList))
        panel.add(loadButton)

        frame.contentPane.add(panel)
        frame.validate()
        frame.repaint()
    }

    private fun saveWorld(world: IsometricWorld, file: File) {
        // Logic to serialize `world` to JSON
        // Write the world state to the file
    }

    private fun startGame(worldName: String) {
        frame.contentPane.removeAll()
        frame.add(IsometricWorld(false, worldName))
        frame.validate()
        frame.repaint()
    }
}
