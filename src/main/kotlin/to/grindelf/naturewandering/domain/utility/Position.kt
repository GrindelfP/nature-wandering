package to.grindelf.naturewandering.domain.utility

data class Position(
    var x: Double,
    var y: Double,
    var targetX: Double?,
    var targetY: Double?,
) {

    override fun toString(): String = "$x,$y,$targetX,$targetY"

    constructor(playerPositionString: String) : this(
        playerPositionString.split("|")[0].split(",")[0].toDouble(),
        playerPositionString.split("|")[0].split(",")[1].toDouble(),
        playerPositionString.split("|")[0].split(",")[2].toDoubleOrNull(),
        playerPositionString.split("|")[0].split(",")[3].toDoubleOrNull(),
    )

}
