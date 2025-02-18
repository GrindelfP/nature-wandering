package to.grindelf.naturewandering.domain.world

import to.grindelf.naturewandering.domain.world.utility.TileType

data class Tile(val x: Int, val y: Int, val type: TileType) {

    override fun toString(): String = "$x,$y,$type"

    constructor(tileString: String) : this(
        tileString.split(",")[0].toInt(),
        tileString.split(",")[1].toInt(),
        TileType.valueOf(tileString.split(",")[2])
    )

}
