package to.grindelf.naturewandering.datamanager.dataop

import to.grindelf.naturewandering.domain.maincharacter.MainCharacter
import to.grindelf.naturewandering.domain.utility.Position
import to.grindelf.naturewandering.domain.utility.WorldState
import java.io.File

object RecordOperator {

    fun saveGameTo(
        saveFile: File,
        worldState: WorldState,
        playerPosition: Position
        ) {

        val worldStateAsString = worldState.toString()
        val playerPositionAsString = playerPosition.toString() // WARNING!!! The player's position
                                                               // should be saved the last

        saveFile.writeText(worldStateAsString + playerPositionAsString)

        // TODO: change the position to MainCharacter
    }

    fun loadGameFrom(saveFile: File): Pair<WorldState, Position> {

        val worldAndPLayerAsString = saveFile.readText()
        val worldAsString = worldAndPLayerAsString.split("||")[0]
        val playerAsString = worldAndPLayerAsString.split("||")[1]

        return Pair<WorldState, Position>(WorldState(worldAsString), Position(playerAsString))

        // TODO: change the position to MainCharacter
    }

}