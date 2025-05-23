package to.grindelf.naturewandering.datamanager.dataop

import to.grindelf.naturewandering.domain.maincharacter.MainCharacter
import to.grindelf.naturewandering.domain.utility.WorldState
import java.io.File

object RecordOperator {

    fun saveGameTo(
        saveFile: File,
        worldState: WorldState,
        mainCharacter: MainCharacter
        ) {

        val worldStateAsString = worldState.toString()
        val mainCharacterAsString = mainCharacter.toString() // WARNING!!! The MainCharacter should be saved the last
        val save = worldStateAsString + mainCharacterAsString
        saveFile.writeText(save)

    }

    fun loadGameFrom(saveFile: File): Pair<WorldState, MainCharacter> {

        val worldAndPLayerAsString = saveFile.readText()
        val worldAsString = worldAndPLayerAsString.split("||")[0]
        val mainCharacterAsString = worldAndPLayerAsString.split("||")[1]

        return Pair<WorldState, MainCharacter>(WorldState.initFromString(worldAsString), MainCharacter(mainCharacterAsString))

        // TODO: change the position to MainCharacter
    }

}