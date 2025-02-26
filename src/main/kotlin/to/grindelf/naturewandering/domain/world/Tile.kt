package to.grindelf.naturewandering.domain.world

import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_SIZE
import to.grindelf.naturewandering.domain.world.utility.TileType

data class Tile(val x: Int, val y: Int, val type: TileType) {

    override fun toString(): String = "$x,$y,$type"

    constructor(index: Int, tileString: String) : this(
        WORLD_SIZE / index,
        WORLD_SIZE % index,
        TileType.valueOf(tileString.split(",")[2])
    )

}
