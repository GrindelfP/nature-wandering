package to.grindelf.naturewandering.domain.maincharacter

import to.grindelf.naturewandering.IsometricWorldConstants
import to.grindelf.naturewandering.domain.utility.Inventory
import to.grindelf.naturewandering.domain.utility.MainCharacterInventory
import to.grindelf.naturewandering.domain.utility.Position
import kotlin.math.sqrt

data class MainCharacter(

    val position: Position = Position(0.0, 0.0, null, null),
    var isMoving: Boolean = false,
    var speed: Double = IsometricWorldConstants.CHARACTER_SPEED_DEFAULT,
    var health: Int = IsometricWorldConstants.CHARACTER_HEALTH_MAX_DEFAULT,
    var level: Int = IsometricWorldConstants.CHARACTER_LEVEL_DEFAULT,
    var inventory: Inventory = MainCharacterInventory("")

) {

    constructor(position: Position) : this(
        position,
        false,
        IsometricWorldConstants.CHARACTER_SPEED_DEFAULT,
        IsometricWorldConstants.CHARACTER_HEALTH_MAX_DEFAULT,
        IsometricWorldConstants.CHARACTER_LEVEL_DEFAULT,
        MainCharacterInventory("")
    )

    constructor(characterAsString: String) : this(
        position = Position(characterAsString.split(";")[0]),
        isMoving = false,
        speed = IsometricWorldConstants.CHARACTER_SPEED_DEFAULT,
        health = characterAsString.split(";")[1].toInt(),
        level = characterAsString.split(";")[2].toInt(),
        inventory = MainCharacterInventory(characterAsString.split(";")[3])
    )

    fun update() {
        val dx = this.position.targetX!! - this.position.x
        val dy = this.position.targetY!! - this.position.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < IsometricWorldConstants.CHARACTER_SPEED_DEFAULT) {
            this.position.x = this.position.targetX!!
            this.position.y = this.position.targetY!!
            this.isMoving = false
        } else {
            this.position.x += (dx / distance) * IsometricWorldConstants.CHARACTER_SPEED_DEFAULT
            this.position.y += (dy / distance) * IsometricWorldConstants.CHARACTER_SPEED_DEFAULT
        }
    }

    override fun toString(): String = "$position;$health;$level;$inventory"

}
