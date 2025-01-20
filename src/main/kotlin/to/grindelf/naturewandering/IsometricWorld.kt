package to.grindelf.naturewandering

import to.grindelf.naturewandering.IsometricWorldConstants.BIRD_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_INITIAL_OFFSET_X
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_INITIAL_OFFSET_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_X
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_INITIAL_X
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_INITIAL_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_SPEED
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.FOOTSTEPS_SOUND_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.FOREST_BACKGROUND_SOUND_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.GRASS_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.INITIAL_SCALE
import to.grindelf.naturewandering.IsometricWorldConstants.NUMBER_OF_BIRDS
import to.grindelf.naturewandering.IsometricWorldConstants.STONE_PROBABILITY
import to.grindelf.naturewandering.IsometricWorldConstants.STONE_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.TILE_SIZE
import to.grindelf.naturewandering.IsometricWorldConstants.TREE2_PROBABILITY
import to.grindelf.naturewandering.IsometricWorldConstants.TREE2_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.TREE_PROBABILITY
import to.grindelf.naturewandering.IsometricWorldConstants.TREE_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_HEIGHT
import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_WIDTH
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_FACTOR
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_LOWER_LIMIT
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_UPPER_LIMIT
import to.grindelf.naturewandering.JsonOperator.saveWorldToFile
import to.grindelf.naturewandering.datamanager.SavesManager
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.*
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.sqrt
import kotlin.random.Random

data class Tile(val x: Int, val y: Int, val type: TileType)

data class Bird(var x: Double, var y: Double, var dx: Double, var dy: Double)

enum class TileType { GRASS, TREE, TREE2, STONE }

class IsometricWorld(
    createWorld: Boolean,
    worldName: String = "world"
) : JPanel(), KeyListener, MouseWheelListener, MouseListener {

    // WORLD
    private val tiles = mutableListOf<Tile>()
    private val birds = mutableListOf<Bird>()

    // TEXTURES
    private lateinit var grassTexture: Image
    private lateinit var treeTexture: Image
    private lateinit var characterTexture: Image
    private lateinit var tree2Texture: Image
    private lateinit var stoneTexture: Image
    private lateinit var birdieTexture: Image

    // SOUNDS
    /**
     * Sound clip of a background forest ambience.
     */
    private lateinit var backgroundSoundClip: Clip
    private lateinit var stepSoundClip: Clip
    private var isStepSoundPlaying = false

    // CAMERA
    private var offsetX = CAMERA_INITIAL_OFFSET_X
    private var offsetY = CAMERA_INITIAL_OFFSET_Y
    private var scale = INITIAL_SCALE

    // CHARACTER
    private var characterX = 0.0 // current position x
    private var characterY = 0.0 // current position y
    private var targetX: Double? = null // where to go by x
    private var targetY: Double? = null // where to go by y
    private var isMoving = false

    init {
        loadTextures()
        loadSounds()

        // Check if the world file exists
        if (createWorld) {
            // Generate new world and save it
            val newWorldFile = SavesManager.createSaveFile(worldName)
            generateWorld()
            saveWorldToFile(tiles, newWorldFile)
        } else {
            // Load existing world
            val worldFile = SavesManager.getSaveFile(worldName)
            loadWorldFrom(worldFile)
        }

        spawnCharacter()
        playBackgroundSound()
        spawnBirds()

        initializeListeners()

        Timer(32) {
            updateCharacter()
            updateBirds()
        }.start()
    }

    private fun initializeListeners() {
        addKeyListener(this)
        addMouseWheelListener(this)
        addMouseListener(this)
        isFocusable = true
    }

    private fun loadTextures() {
        try {
            val grassTextureInputStream = javaClass.getResourceAsStream(GRASS_TEXTURE_PATH)
            val treeTextureInputStream = javaClass.getResourceAsStream(TREE_TEXTURE_PATH)
            val tree2TextureInputStream = javaClass.getResourceAsStream(TREE2_TEXTURE_PATH)
            val stoneTextureInputStream = javaClass.getResourceAsStream(STONE_TEXTURE_PATH)
            val characterTextureInputStream = javaClass.getResourceAsStream(CHARACTER_TEXTURE_PATH)
            val birdieTextureInputStream = javaClass.getResourceAsStream(BIRD_TEXTURE_PATH)

            grassTexture = ImageIO.read(grassTextureInputStream)
            treeTexture = ImageIO.read(treeTextureInputStream)
            tree2Texture = ImageIO.read(tree2TextureInputStream)
            stoneTexture = ImageIO.read(stoneTextureInputStream)
            characterTexture = ImageIO.read(characterTextureInputStream)
            birdieTexture = ImageIO.read(birdieTextureInputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            error("Unable to load textures!!")
        }
    }

    private fun loadSounds() {

        try {
            val backgroundSoundInputStream = javaClass.getResourceAsStream(FOREST_BACKGROUND_SOUND_PATH)
            val stepsSoundInputStream = javaClass.getResourceAsStream(FOOTSTEPS_SOUND_PATH)

            if (backgroundSoundInputStream != null && stepsSoundInputStream != null) {
                backgroundSoundClip = initializeSoundClipFrom(backgroundSoundInputStream, volume = -30.0f)
                stepSoundClip = initializeSoundClipFrom(stepsSoundInputStream, volume = -5.0f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error("Unable to load sounds!!")
        }

    }

    fun generateWorld() {
        for (x in 0 until WORLD_WIDTH) {
            for (y in 0 until WORLD_HEIGHT) {
                val randomValue = Random.nextFloat()
                val tileType = when {
                    randomValue < STONE_PROBABILITY -> TileType.STONE  // Stone (10%)
                    randomValue < TREE_PROBABILITY -> TileType.TREE   // Trees of the first type (20%)
                    randomValue < TREE2_PROBABILITY -> TileType.TREE2  // Trees of the second type (20%)
                    else -> TileType.GRASS // Grass (50%)
                }
                tiles.add(Tile(x, y, tileType)) // Complete the list of tiles
            }
        }
    }

    fun loadWorldFrom(worldFile: File) {
        tiles.addAll(JsonOperator.loadWorldFromFile(worldFile))
    }

    /**
     * Plays background sound taken from backgroundSoundClip property.
     */
    private fun playBackgroundSound() {
        backgroundSoundClip.loop(Clip.LOOP_CONTINUOUSLY)
    }

    /**
     * Function initializes sound clip from a provided input stream.
     * @param soundInputStream stream from a sound resource
     * @param volume the amount of volume to be set for the clip, default value is -10.0.
     *
     * @return sound clip from a provided stream with provided or default volume set.
     */
    private fun initializeSoundClipFrom(soundInputStream: InputStream, volume: Float = -10.0f): Clip {
        val clip: Clip = AudioSystem.getClip()

        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(BufferedInputStream(soundInputStream))
        clip.open(audioInputStream)
        val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        gainControl.value = volume

        return clip
    }

    private fun playStepSound() {
        stepSoundClip.loop(Clip.LOOP_CONTINUOUSLY)

        if (!isStepSoundPlaying) {
            stepSoundClip.start()
            isStepSoundPlaying = true
        }
    }

    private fun stopStepSound() {
        if (isStepSoundPlaying) {
            stepSoundClip.stop()
            stepSoundClip.framePosition = 0
            isStepSoundPlaying = false
        }
    }

    private fun spawnCharacter() {
        val centerTile = tiles.find { it.x == CHARACTER_INITIAL_X && it.y == CHARACTER_INITIAL_Y }
        characterX = centerTile?.x?.toDouble() ?: 0.0
        characterY = centerTile?.y?.toDouble() ?: 0.0
    }

    private fun updateCharacter() {
        if (isMoving && targetX != null && targetY != null) {
            playStepSound()

            val dx = targetX!! - characterX
            val dy = targetY!! - characterY
            val distance = sqrt(dx * dx + dy * dy)

            if (distance < CHARACTER_SPEED) {
                characterX = targetX!!
                characterY = targetY!!
                isMoving = false
                stopStepSound()
            } else {
                characterX += (dx / distance) * CHARACTER_SPEED
                characterY += (dy / distance) * CHARACTER_SPEED
            }
            repaint()
        } else {
            stopStepSound()
        }
    }


    private fun spawnBirds() {
        repeat(NUMBER_OF_BIRDS) {
            birds.add(
                Bird(
                    x = Random.nextDouble(0.0, WORLD_WIDTH.toDouble()),
                    y = Random.nextDouble(0.0, WORLD_HEIGHT.toDouble()),
                    dx = Random.nextDouble(-0.1, 0.1),
                    dy = Random.nextDouble(-0.1, 0.1)
                )
            )
        }
    }

    private fun updateBirds() {
        for (bird in birds) {
            bird.x += bird.dx
            bird.y += bird.dy

            if (bird.x < 0 || bird.x >= WORLD_WIDTH) {
                bird.dx = -bird.dx
                bird.x = bird.x.coerceIn(0.0, WORLD_WIDTH.toDouble())
            }
            if (bird.y < 0 || bird.y >= WORLD_HEIGHT) {
                bird.dy = -bird.dy
                bird.y = bird.y.coerceIn(0.0, WORLD_HEIGHT.toDouble())
            }
        }
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        g2d.scale(scale, scale)

        // LAYER 1: SURFACE
        for (tile in tiles) {
            val screenX = ((tile.x - tile.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((tile.x + tile.y) * TILE_SIZE / 4 + offsetY / scale).toInt()

            // Initially all tiles are drawn as grass
            g2d.drawImage(
                grassTexture,
                screenX - TILE_SIZE / 2,
                screenY - TILE_SIZE / 4,
                TILE_SIZE,
                TILE_SIZE / 2,
                null
            )

            when (tile.type) {

                // Next stones are drawn
                TileType.STONE -> {
                    g2d.drawImage(
                        stoneTexture,
                        screenX - TILE_SIZE / 4,
                        screenY - TILE_SIZE / 4,
                        TILE_SIZE / 2,
                        TILE_SIZE / 2,
                        null
                    )
                }

                else -> {}
            }
        }

        // LAYER 2: CHARACTER
        val characterScreenX = ((characterX - characterY) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
        val characterScreenY = ((characterX + characterY) * TILE_SIZE / 4 + offsetY / scale).toInt()
        g2d.drawImage(
            characterTexture,
            characterScreenX - TILE_SIZE / 6,
            characterScreenY - TILE_SIZE / 3,
            TILE_SIZE / 3,
            TILE_SIZE / 3,
            null
        )

        // LAYER 3: TREES
        for (tile in tiles) {
            val screenX = ((tile.x - tile.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((tile.x + tile.y) * TILE_SIZE / 4 + offsetY / scale).toInt()

            when (tile.type) {
                TileType.TREE -> {
                    val treeHeight = TILE_SIZE
                    val treeWidth = TILE_SIZE / 2
                    g2d.drawImage(
                        treeTexture,
                        screenX - treeWidth / 2,
                        screenY - treeHeight,
                        treeWidth * 2,
                        treeHeight,
                        null
                    )
                }

                TileType.TREE2 -> {
                    val treeHeight = TILE_SIZE
                    val treeWidth = TILE_SIZE / 2
                    g2d.drawImage(
                        tree2Texture,
                        screenX - treeWidth / 2,
                        screenY - treeHeight,
                        treeWidth * 2,
                        treeHeight,
                        null
                    )
                }

                else -> {}
            }
        }

        // LAYER 4: BIRDS
        for (bird in birds) {
            val screenX = ((bird.x - bird.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((bird.x + bird.y) * TILE_SIZE / 4 + offsetY / scale).toInt()
            g2d.drawImage(
                birdieTexture,
                screenX - TILE_SIZE / 8,
                screenY - TILE_SIZE / 8,
                TILE_SIZE / 4,
                TILE_SIZE / 4,
                null
            )
        }
    }


    // INPUT HANDLING
    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_W -> offsetY += CAMERA_MOVEMENT_LENGTH_Y  // UP
            KeyEvent.VK_S -> offsetY -= CAMERA_MOVEMENT_LENGTH_Y  // DOWN
            KeyEvent.VK_A -> offsetX += CAMERA_MOVEMENT_LENGTH_X  // LEFT
            KeyEvent.VK_D -> offsetX -= CAMERA_MOVEMENT_LENGTH_X  // RIGHT
        }
        repaint()
    }

    override fun mouseClicked(e: MouseEvent) {
        val clickX = (e.x / scale) - (width / 2 / scale) - offsetX / scale
        val clickY = (e.y / scale) - offsetY / scale

        val worldX = ((clickX / (TILE_SIZE / 2)) + (clickY / (TILE_SIZE / 4))) / 2
        val worldY = ((clickY / (TILE_SIZE / 4)) - (clickX / (TILE_SIZE / 2))) / 2

        targetX = worldX
        targetY = worldY
        isMoving = true
    }

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        val notches = e?.wheelRotation
        val zoomFactor = ZOOM_FACTOR

        scale = (scale - (notches?.times(zoomFactor) ?: 0.0)).coerceIn(ZOOM_LOWER_LIMIT, ZOOM_UPPER_LIMIT)

        repaint()
    }

    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}
    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
}
