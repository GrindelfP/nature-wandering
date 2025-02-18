package to.grindelf.naturewandering.domain.animals

/**
 * I'm a bird motherf...r I'm a bird
 * Look at me motherf...r I'm a bird
 */
data class Bird(var x: Double, var y: Double, var dx: Double, var dy: Double) {

    override fun toString(): String = "$x,$y,$dx,$dy"

    constructor(birdString: String) : this(
        birdString.split(",")[0].toDouble(),
        birdString.split(",")[1].toDouble(),
        birdString.split(",")[2].toDouble(),
        birdString.split(",")[3].toDouble()
    )

}
