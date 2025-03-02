package to.grindelf.naturewandering.domain.utility

import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_SIZE
import to.grindelf.naturewandering.domain.animals.Bird
import to.grindelf.naturewandering.domain.world.Tile

data class WorldState(
    val tiles: MutableList<Tile>,
    val birds: MutableList<Bird>,
    val worldSize: Int = WORLD_SIZE
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

    constructor(worldStateString: String) : this(
        worldStateString.split("|")[0].split(";").filter { it.isNotEmpty() }.mapIndexed { i, it -> Tile(i, it, worldSize - 1) }.toMutableList(),
        worldStateString.split("|")[1].split(";").filter { it.isNotEmpty() }.map { Bird(it) }.toMutableList()
    )
}
