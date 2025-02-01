package to.grindelf.naturewandering.datamanager.dataop

import to.grindelf.naturewandering.domain.utility.Position
import to.grindelf.naturewandering.domain.utility.WorldState
import java.io.File
import java.io.InputStream

object RecordOperator {

    fun saveGameTo(
        saveFile: File,
        worldState: WorldState,
        playerPosition: Position
        ) {

        val worldStateAsString = worldState.toString()
        val playerPositionAsString = playerPosition.toString()

        saveFile.writeText(worldStateAsString + playerPositionAsString)
    }

    fun loadGameFrom(stream: InputStream): Pair<WorldState, Position> {

        val worldAsString = stream.read()

        TODO()
    }

}