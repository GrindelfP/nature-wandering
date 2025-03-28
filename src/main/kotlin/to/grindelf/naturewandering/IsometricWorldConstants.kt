package to.grindelf.naturewandering

object IsometricWorldConstants {

    const val TILE_SIZE = 64

    const val WORLD_SIZE = 80

    const val WORLD_WIDTH = WORLD_SIZE
    const val WORLD_HEIGHT = WORLD_SIZE

    const val CAMERA_INITIAL_OFFSET_X = 0
    const val CAMERA_INITIAL_OFFSET_Y = -500

    const val CHARACTER_INITIAL_X = 10
    const val CHARACTER_INITIAL_Y = 10

    const val CHARACTER_SPEED_DEFAULT = 0.05
    const val CHARACTER_HEALTH_MAX_DEFAULT = 10
    const val CHARACTER_HEALTH_MIN = 0
    const val CHARACTER_LEVEL_DEFAULT = 1
    const val CHARACTER_LEVEL_MAX = 12

    const val CAMERA_MOVEMENT_LENGTH_X = 20
    const val CAMERA_MOVEMENT_LENGTH_Y = 20

    const val INITIAL_SCALE = 3.5

    const val ZOOM_FACTOR = 0.1

    const val ZOOM_LOWER_LIMIT = 1.0
    const val ZOOM_UPPER_LIMIT = 4.0

    const val SAVES_PATH = "GrindelfStudios/NatureWondering/saves"

    const val GRASS_TEXTURE_PATH = "assets/textures/grass.png"
    const val CHARACTER_TEXTURE_PATH = "assets/textures/character.png"
    const val TREE_TEXTURE_PATH = "assets/textures/tree.png"
    const val TREE2_TEXTURE_PATH = "assets/textures/tree2.png"
    const val STONE_TEXTURE_PATH = "assets/textures/stone.png"
    const val BIRD_TEXTURE_PATH = "assets/textures/birdie.png"

    const val GRASS_TEXTURES_PATH = "assets/textures/grass"
    const val STONES_TEXTURES_PATH = "assets/textures/stones"
    const val TREES_TEXTURES_PATH = "assets/textures/trees"
    const val PORTAL_TEXTURES_PATH = "assets/textures/portal"
    const val CHARACTER_TEXTURES_PATH = "assets/textures/character"
    const val JACK_TEXTURES_PATH = "assets/textures/jack"

    const val FOREST_BACKGROUND_SOUND_PATH = "assets/sounds/forest.wav"
    const val FOOTSTEPS_SOUND_PATH = "assets/sounds/footsteps.wav"

    const val STONE_PROBABILITY = 0.1
    const val TREE_PROBABILITY = 0.3
    const val TREE2_PROBABILITY = 0.5

    const val WINDOW_NAME = "Nature Wandering"
    const val WINDOW_WIDTH = 1600
    const val WINDOW_HEIGHT = 1200

    const val NUMBER_OF_BIRDS = 50
}
