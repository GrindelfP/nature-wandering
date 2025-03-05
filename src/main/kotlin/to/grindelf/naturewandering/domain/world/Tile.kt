package to.grindelf.naturewandering.domain.world

import to.grindelf.naturewandering.domain.world.utility.TileType

data class Tile(val x: Int, val y: Int, val type: TileType) {

    override fun toString(): String = type.code

    constructor(index: Int, tileString: String, upperIndexLimit: Int) : this(
        if (index == 0) 0 else index / upperIndexLimit,
        if (index == 0) 0 else index % upperIndexLimit,
        TileType.entries.firstOrNull { it.code == tileString} ?: TileType.NULL_TYPE
    )
}
