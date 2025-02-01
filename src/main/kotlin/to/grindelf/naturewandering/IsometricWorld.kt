package to.grindelf.naturewandering

import to.grindelf.naturewandering.IsometricWorldConstants.BIRD_TEXTURE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_INITIAL_OFFSET_X
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_INITIAL_OFFSET_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_X
import to.grindelf.naturewandering.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_INITIAL_X
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_INITIAL_Y
import to.grindelf.naturewandering.IsometricWorldConstants.CHARACTER_SPEED_DEFAULT
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
import to.grindelf.naturewandering.domain.maincharacter.MainCharacter
import to.grindelf.naturewandering.domain.utility.Position
import to.grindelf.naturewandering.datamanager.dataop.JsonOperator.saveWorldToFile
import to.grindelf.naturewandering.datamanager.saves.SavesManager
import to.grindelf.naturewandering.datamanager.dataop.JsonOperator
import to.grindelf.naturewandering.domain.animals.Bird
import to.grindelf.naturewandering.domain.utility.WorldState
import to.grindelf.naturewandering.domain.world.Tile
import to.grindelf.naturewandering.domain.world.utility.TileType
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
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.exitProcess


class IsometricWorld(
    createWorld: Boolean,
    worldName: String = "world",
    private val frame: JFrame,  // Reference to the frame
    private val mainMenuPanel: JPanel  // Reference to the main menu panel
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
    private val mainCharacter: MainCharacter = spawnMainCharacter()

    // GAME STATE
    private var paused: Boolean = false

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

        playBackgroundSound()
        spawnBirds()

        initializeListeners()

        Timer(32) {
            updateCharacters()
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

            requireNotNull(backgroundSoundInputStream) {
                "Background forest sounds stream is null!!"
            }
            requireNotNull(stepsSoundInputStream) {
                "Step sounds stream is null!!"
            }
            backgroundSoundClip = initializeSoundClipFrom(backgroundSoundInputStream, volume = -30.0f)
            stepSoundClip = initializeSoundClipFrom(stepsSoundInputStream, volume = -5.0f)
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

    private fun spawnMainCharacter(): MainCharacter {
        val centerTile = tiles.find {
            it.x == CHARACTER_INITIAL_X && it.y == CHARACTER_INITIAL_Y
        }
        val xPosition = centerTile?.x?.toDouble() ?: 0.0
        val yPosition = centerTile?.y?.toDouble() ?: 0.0

        val character = MainCharacter(
            Position(xPosition, yPosition, null, null)
        )

        return character
    }

    private fun updateCharacters() {

        updateMainCharacter()
    }

    private fun updateMainCharacter() {
        if (!paused && mainCharacter.isMoving && mainCharacter.position.targetX != null && mainCharacter.position.targetY != null) {
            playStepSound()
            mainCharacter.update()
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

        if (!paused) {

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
        val characterScreenX =
            ((mainCharacter.position.x - mainCharacter.position.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
        val characterScreenY = ((mainCharacter.position.x + mainCharacter.position.y) * TILE_SIZE / 4 + offsetY / scale).toInt()
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
            KeyEvent.VK_ESCAPE -> {
                if (!paused) {
                    showPauseMenu()
                } else {
                    paused = false
                }
            }  // Show the pause menu on ESC key press
        }
        repaint()
    }

    private fun showPauseMenu() {
        // Options for the pause menu
        val options = arrayOf("Continue", "Back to Main Menu", "Exit Game")

        this.paused = true

        // Show the dialog with buttons
        val choice = JOptionPane.showOptionDialog(
            this,
            "Game Paused",
            "Pause Menu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]  // Default option to "Continue"
        )

        when (choice) {
            0 -> {
                this.paused = false
            }  // Continue: Do nothing, just close the menu
            1 -> backToMainMenu(frame, mainMenuPanel)  // Back to Main Menu
            2 -> exitGame()  // Exit the game
        }
    }

    private fun backToMainMenu(frame: JFrame, mainMenuPanel: JPanel) {
        println("Returning to main menu...")

        // Remove the current game panel
        frame.contentPane.removeAll()

        // Add the main menu panel back
        frame.contentPane.add(mainMenuPanel)

        // Revalidate and repaint to show the main menu
        frame.revalidate()
        frame.repaint()
    }


    private fun exitGame() {
        // Exit the game by closing the application
        println("Exiting the game...")
        exitProcess(0)  // This will close the application
    }

    override fun mouseClicked(e: MouseEvent) {
        val clickX = (e.x / scale) - (width / 2 / scale) - offsetX / scale
        val clickY = (e.y / scale) - offsetY / scale

        val worldX = ((clickX / (TILE_SIZE / 2)) + (clickY / (TILE_SIZE / 4))) / 2
        val worldY = ((clickY / (TILE_SIZE / 4)) - (clickX / (TILE_SIZE / 2))) / 2

        mainCharacter.position.targetX = worldX
        mainCharacter.position.targetY = worldY
        mainCharacter.isMoving = true
    }

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        val notches = e?.wheelRotation
        val zoomFactor = ZOOM_FACTOR

        scale = (scale - (notches?.times(zoomFactor) ?: 0.0)).coerceIn(ZOOM_LOWER_LIMIT, ZOOM_UPPER_LIMIT)

        repaint()
    }

    override fun keyReleased(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_ESCAPE -> paused = false
        }
    }

    override fun keyTyped(e: KeyEvent) {}
    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}

    fun state(): WorldState = WorldState(tiles, birds)
    // fun playerPosition(): Position = mainCharacter.position
}
