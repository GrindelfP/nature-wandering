package to.grindelf.naturewandering.assets.characters.maincharacter

import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_HEALTH_MAX_DEFAULT
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_LEVEL_DEFAULT
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_SPEED_DEFAULT
import to.grindelf.naturewandering.assets.characters.utility.Inventory
import to.grindelf.naturewandering.assets.characters.utility.MainCharacterInventory
import to.grindelf.naturewandering.assets.characters.utility.Position

data class MainCharacter(

    val position: Position = Position(0.0, 0.0, null, null),
    var isMoving: Boolean = false,
    var health: Int = CHARACTER_HEALTH_MAX_DEFAULT,
    var speed: Double = CHARACTER_SPEED_DEFAULT,
    var level: Int = CHARACTER_LEVEL_DEFAULT,
    var inventory: Inventory = MainCharacterInventory()

)
