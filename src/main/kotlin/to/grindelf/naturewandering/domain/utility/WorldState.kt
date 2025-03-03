package to.grindelf.naturewandering.domain.utility

import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_SIZE
import to.grindelf.naturewandering.domain.animals.Bird
import to.grindelf.naturewandering.domain.exceptions.SaveFileException
import to.grindelf.naturewandering.domain.world.Tile

data class WorldState(
    val worldSize: Int = WORLD_SIZE,
    val tiles: MutableList<Tile>,
    val birds: MutableList<Bird>
) {

    override fun toString(): String {

        var resultString = ""
        tiles.forEach { tile ->
            resultString += "$tile;"
        }
        resultString += "|"
        birds.forEach { bird ->
            resultString += "$bird;"
        }

        return "$resultString||"
    }

    companion object {
        fun initFromString(worldStateString: String): WorldState {

            val worldStateParams = worldStateString.split("|")

            val worldSizeString = worldStateParams[0]
            require(worldSizeString.isNotEmpty()) {
                throw SaveFileException("World size is missing!")
            }
            requireNotNull(worldSizeString.toIntOrNull()) {
                throw SaveFileException("World size cannot be interpreted as an integer value!")
            }
            val worldSize = worldSizeString.toInt()

            val tiles = worldStateParams[1].split(";").filter { tileString ->
                tileString.isNotEmpty()
            }.mapIndexed { i, tileString ->
                Tile(i, tileString, worldSize - 1)
            }.toMutableList()
            require(tiles.isNotEmpty()) {
                throw SaveFileException("Tiles weren't read from the save file!")
            }

            val birrrrrrds = worldStateParams[2].split(";").filter { birdString ->
                birdString.isNotEmpty()
            }.map { birdString ->
                Bird(birdString)
            }.toMutableList()
            require(birrrrrrds.isNotEmpty()) {
                throw SaveFileException("Birds weren't read from the save file!")
            }

            return WorldState(worldSize, tiles, birrrrrrds)
        }
    }
}
