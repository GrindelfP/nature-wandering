package to.grindelf.naturewandering.datamanager.saves

import to.grindelf.naturewandering.IsometricWorldConstants
import java.io.File

object SavesManager {

    fun getSaveFile(worldName: String = "world"): File = File(getSavesDirectory(), "$worldName.json")

    /**
     * Creates a new savefile.json for the world with the provided or default name.
     *
     * @param worldName name for the created savefile of world (default value is "world")
     *
     * @return newSaveFile - the newly created savefile
     */
    fun createSaveFile(worldName: String = "world"): File {
        val newSaveFile = File(getSavesDirectory(), "$worldName.json")

        // Ensure the save directory exists
        if (!newSaveFile.parentFile.exists()) {
            newSaveFile.parentFile.mkdirs()
        }

        // Create the save file if it doesn't exist
        if (!newSaveFile.exists()) {
            newSaveFile.createNewFile()
        }

        return newSaveFile
    }

    fun getDocumentsDirectory(): File {
        val userHome = System.getProperty("user.home")
        val documentsPath = when {
            System.getProperty("os.name").contains("Windows", ignoreCase = true) -> "$userHome\\Documents"
            else -> "$userHome/Documents"
        }
        return File(documentsPath)
    }

    fun getSavesDirectory(): File {
        val savesDir = File(getDocumentsDirectory(), IsometricWorldConstants.SAVES_PATH)
        if (!savesDir.exists()) {
            savesDir.mkdirs() // Create directory structure if it doesn't exist
        }
        return savesDir
    }

    fun ifSavesDirExistAndNotEmpty(): Boolean {
        var exists = false

        val savesDir = getSavesDirectory()
        if (savesDir.exists() && savesDir.isDirectory && !savesDir.listFiles().isNullOrEmpty()) exists = true

        return exists
    }
}