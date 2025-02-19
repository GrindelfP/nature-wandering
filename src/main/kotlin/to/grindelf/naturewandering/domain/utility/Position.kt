package to.grindelf.naturewandering.domain.utility

data class Position(
    var x: Double,
    var y: Double,
    var targetX: Double?,
    var targetY: Double?,
) {

    override fun toString(): String = "$x,$y"

    constructor(playerPositionString: String) : this(
        playerPositionString.split(",")[0].toDouble(),
        playerPositionString.split(",")[1].toDouble(),
        null,
        null,
    )

}
