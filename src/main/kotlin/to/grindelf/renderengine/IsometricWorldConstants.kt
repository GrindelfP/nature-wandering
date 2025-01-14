package to.grindelf.renderengine

object IsometricWorldConstants {

    const val TILE_SIZE = 64

    const val WORLD_WIDTH = 80
    const val WORLD_HEIGHT = 80

    const val CAMERA_INITIAL_OFFSET_X = 0
    const val CAMERA_INITIAL_OFFSET_Y = -500

    const val CHARACTER_INITIAL_X = 10
    const val CHARACTER_INITIAL_Y = 10

    const val CHARACTER_SPEED = 0.05

    const val CAMERA_MOVEMENT_LENGTH_X = 20
    const val CAMERA_MOVEMENT_LENGTH_Y = 20

    const val INITIAL_SCALE = 3.5

    const val ZOOM_FACTOR = 0.1

    const val ZOOM_LOWER_LIMIT = 1.0
    const val ZOOM_UPPER_LIMIT = 4.0

    const val GRASS_TEXTURE_PATH = "src/main/resources/assets/textures/grass.png"
    const val CHARACTER_TEXTURE_PATH = "src/main/resources/assets/textures/character.png"
    const val TREE_TEXTURE_PATH = "src/main/resources/assets/textures/tree.png"
    const val TREE2_TEXTURE_PATH = "src/main/resources/assets/textures/tree2.png"
    const val STONE_TEXTURE_PATH = "src/main/resources/assets/textures/stone.png"
    const val BIRD_TEXTURE_PATH = "src/main/resources/assets/textures/birdie.png"
    const val WORLD_FILE_PATH = "src/main/resources/world/world.json"

    const val FOREST_BACKGROUND_SOUND_PATH = "src/main/resources/assets/sounds/forest.wav"
    const val FOOTSTEPS_SOUND_PATH = "src/main/resources/assets/sounds/footsteps.wav"

    const val STONE_PROBABILITY = 0.1
    const val TREE_PROBABILITY = 0.3
    const val TREE2_PROBABILITY = 0.5

    const val WINDOW_NAME = "2D Isometric World"
    const val WINDOW_WIDTH = 1600
    const val WINDOW_HEIGHT = 1200

    const val NUMBER_OF_BIRDS = 50
}
