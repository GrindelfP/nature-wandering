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
import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_FILE_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_HEIGHT
import to.grindelf.naturewandering.IsometricWorldConstants.WORLD_WIDTH
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_FACTOR
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_LOWER_LIMIT
import to.grindelf.naturewandering.IsometricWorldConstants.ZOOM_UPPER_LIMIT
import to.grindelf.naturewandering.JsonOperator.loadWorldFromFile
import to.grindelf.naturewandering.JsonOperator.saveWorldToFile
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.*
import java.io.BufferedInputStream
import java.io.File
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

class IsometricWorld (
    createWorld: Boolean
) : JPanel(), KeyListener, MouseWheelListener, MouseListener {

    internal val tiles = mutableListOf<Tile>()
    private lateinit var grassTexture: Image
    private lateinit var treeTexture: Image
    private lateinit var characterTexture: Image
    private lateinit var tree2Texture: Image
    private lateinit var stoneTexture: Image
    private lateinit var birdieTexture: Image

    private val worldFile = File(WORLD_FILE_PATH)

    // Смещение "камеры"
    private var offsetX = CAMERA_INITIAL_OFFSET_X
    private var offsetY = CAMERA_INITIAL_OFFSET_Y

    // Масштаб
    private var scale = INITIAL_SCALE

    // Персонаж
    private var characterX = 0.0
    private var characterY = 0.0
    private var targetX: Double? = null
    private var targetY: Double? = null
    private var isMoving = false

    private val birds = mutableListOf<Bird>()

    // Sound Clips
    private lateinit var stepClip: Clip
    private var isStepSoundPlaying = false

    init {
        loadTextures()

        // Check if the world file exists
        if (!createWorld) {
            // Load existing world
            tiles.addAll(loadWorldFromFile(worldFile.path))
        } else {
            // Generate new world and save it
            generateWorld()
            saveWorldToFile(tiles, worldFile.path)
        }

        spawnCharacter()
        playBackgroundSound()
        initializeStepSound()
        spawnBirds()

        addKeyListener(this)
        addMouseWheelListener(this)
        addMouseListener(this)
        isFocusable = true

        Timer(32) {
            updateCharacter()
            updateBirds()
        }.start()
    }

    private fun loadTextures() {
        try {
            // Загрузка текстур из ресурсов
            grassTexture = ImageIO.read(File(GRASS_TEXTURE_PATH))
            treeTexture = ImageIO.read(File(TREE_TEXTURE_PATH))
            tree2Texture = ImageIO.read(File(TREE2_TEXTURE_PATH))
            stoneTexture = ImageIO.read(File(STONE_TEXTURE_PATH))
            characterTexture = ImageIO.read(File(CHARACTER_TEXTURE_PATH))
            birdieTexture = ImageIO.read(File(BIRD_TEXTURE_PATH))
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Не удалось загрузить текстуры!")
        }
    }


    internal fun generateWorld() {
        for (x in 0 until WORLD_WIDTH) {
            for (y in 0 until WORLD_HEIGHT) {
                val randomValue = Random.nextFloat()
                val tileType = when {
                    randomValue < STONE_PROBABILITY -> TileType.STONE  // Камень (10% вероятность)
                    randomValue < TREE_PROBABILITY -> TileType.TREE   // Первый тип дерева (20% вероятность)
                    randomValue < TREE2_PROBABILITY -> TileType.TREE2  // Второй тип дерева (20% вероятность)
                    else -> TileType.GRASS              // Трава (50% вероятность)
                }
                tiles.add(Tile(x, y, tileType))
            }
        }
    }

    private fun playBackgroundSound() {
        playSound(soundFile = File(FOREST_BACKGROUND_SOUND_PATH))
    }

    private fun playSound(soundFile: File, volume: Float = -30.0f) {
        try {
            val audioInputStream: AudioInputStream =
                AudioSystem.getAudioInputStream(BufferedInputStream(soundFile.inputStream()))
            val clip: Clip = AudioSystem.getClip()
            clip.open(audioInputStream)

            val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gainControl.value = volume

            clip.loop(Clip.LOOP_CONTINUOUSLY) // Loop the sound continuously
            clip.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeStepSound() {
        stepClip = loadSound(File(FOOTSTEPS_SOUND_PATH))
        stepClip.loop(Clip.LOOP_CONTINUOUSLY)

        playStepSound()
        stopStepSound()
    }

    private fun loadSound(soundFile: File, volume: Float = -10.0f): Clip {
        val audioInputStream: AudioInputStream =
            AudioSystem.getAudioInputStream(BufferedInputStream(soundFile.inputStream()))
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        gainControl.value = volume
        return clip
    }

    private fun playStepSound() {
        stepClip.loop(Clip.LOOP_CONTINUOUSLY)

        if (!isStepSoundPlaying) {
            stepClip.start()
            isStepSoundPlaying = true
        }
    }

    private fun stopStepSound() {
        if (isStepSoundPlaying) {
            stepClip.stop()
            stepClip.framePosition = 0 // Reset to the beginning
            isStepSoundPlaying = false
        }
    }

    private fun spawnCharacter() {
        // Устанавливаем персонажа в центр карты
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

            // Проверка на выход за пределы карты и смена направления
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

        // Масштабируем графику
        g2d.scale(scale, scale)

        for (tile in tiles) {
            val screenX = ((tile.x - tile.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((tile.x + tile.y) * TILE_SIZE / 4 + offsetY / scale).toInt()

            g2d.drawImage(
                grassTexture,
                screenX - TILE_SIZE / 2,
                screenY - TILE_SIZE / 4,
                TILE_SIZE,
                TILE_SIZE / 2,
                null
            )

            when (tile.type) {

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

        // Отрисовка персонажа (после всех объектов)
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

        // Теперь рисуем деревья, которые будут перекрывать фон
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


    // Обработка нажатий клавиш
    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_W -> offsetY += CAMERA_MOVEMENT_LENGTH_Y  // Движение вверх
            KeyEvent.VK_S -> offsetY -= CAMERA_MOVEMENT_LENGTH_Y  // Движение вниз
            KeyEvent.VK_A -> offsetX += CAMERA_MOVEMENT_LENGTH_X  // Движение влево
            KeyEvent.VK_D -> offsetX -= CAMERA_MOVEMENT_LENGTH_X  // Движение вправо
        }
        repaint()  // Перерисовываем мир после изменения смещения
    }

    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}

    // Обработка клика мыши
    override fun mouseClicked(e: MouseEvent) {
        // Координаты клика на экране с учетом масштаба
        val clickX = (e.x / scale) - (width / 2 / scale) - offsetX / scale
        val clickY = (e.y / scale) - offsetY / scale

        // Преобразование экранных координат в изометрические координаты
        val worldX = ((clickX / (TILE_SIZE / 2)) + (clickY / (TILE_SIZE / 4))) / 2
        val worldY = ((clickY / (TILE_SIZE / 4)) - (clickX / (TILE_SIZE / 2))) / 2

        // Устанавливаем целевую точку
        targetX = worldX
        targetY = worldY
        isMoving = true
    }

    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}

    // Обработка прокрутки колесика мыши
    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        val notches = e?.wheelRotation
        val zoomFactor = ZOOM_FACTOR

        // Уменьшаем масштаб, если крутим вниз, увеличиваем - если вверх
        scale = (scale - (notches?.times(zoomFactor) ?: 0.0)).coerceIn(ZOOM_LOWER_LIMIT, ZOOM_UPPER_LIMIT)

        repaint()  // Перерисовка после изменения масштаба
    }
}
