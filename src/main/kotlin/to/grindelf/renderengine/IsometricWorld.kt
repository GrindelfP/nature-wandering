package to.grindelf.renderengine

import to.grindelf.renderengine.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_X
import to.grindelf.renderengine.IsometricWorldConstants.CAMERA_MOVEMENT_LENGTH_Y
import to.grindelf.renderengine.IsometricWorldConstants.CHARACTER_INITIAL_X
import to.grindelf.renderengine.IsometricWorldConstants.CHARACTER_INITIAL_Y
import to.grindelf.renderengine.IsometricWorldConstants.CHARACTER_TEXTURE_PATH
import to.grindelf.renderengine.IsometricWorldConstants.GRASS_TEXTURE_PATH
import to.grindelf.renderengine.IsometricWorldConstants.STONE_PROBABILITY
import to.grindelf.renderengine.IsometricWorldConstants.STONE_TEXTURE_PATH
import to.grindelf.renderengine.IsometricWorldConstants.TILE_SIZE
import to.grindelf.renderengine.IsometricWorldConstants.TREE2_PROBABILITY
import to.grindelf.renderengine.IsometricWorldConstants.TREE2_TEXTURE_PATH
import to.grindelf.renderengine.IsometricWorldConstants.TREE_PROBABILITY
import to.grindelf.renderengine.IsometricWorldConstants.TREE_TEXTURE_PATH
import to.grindelf.renderengine.IsometricWorldConstants.WORLD_HEIGHT
import to.grindelf.renderengine.IsometricWorldConstants.WORLD_WIDTH
import to.grindelf.renderengine.IsometricWorldConstants.ZOOM_FACTOR
import to.grindelf.renderengine.IsometricWorldConstants.ZOOM_LOWER_LIMIT
import to.grindelf.renderengine.IsometricWorldConstants.ZOOM_UPPER_LIMIT
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.sqrt
import kotlin.random.Random

data class Tile(val x: Int, val y: Int, val type: TileType)

enum class TileType { GRASS, TREE, TREE2, STONE }

class IsometricWorld : JPanel(), KeyListener, MouseWheelListener, MouseListener {

    private val tiles = mutableListOf<Tile>()
    private lateinit var grassTexture: Image
    private lateinit var treeTexture: Image
    private lateinit var characterTexture: Image
    private lateinit var tree2Texture: Image
    private lateinit var stoneTexture: Image

    // Смещение "камеры"
    private var offsetX = 0
    private var offsetY = -300  // Сдвиг мира вниз

    // Масштаб
    private var scale = 2.0  // 1.0 - оригинальный размер, > 1.0 - увеличение, < 1.0 - уменьшение

    // Персонаж
    private var characterX = 0.0
    private var characterY = 0.0
    private var targetX: Double? = null
    private var targetY: Double? = null
    private val characterSpeed = 0.3 // Скорость персонажа (пикселей за тик)
    private var isMoving = false

    init {
        // Загрузка текстур
        loadTextures()

        // Генерация карты
        generateWorld()

        // Установка персонажа в центр карты
        placeCharacterInCenter()

        // Добавляем обработчики событий
        addKeyListener(this)
        addMouseWheelListener(this)
        addMouseListener(this)
        isFocusable = true

        // Таймер для обновления персонажа
        Timer(32) { updateCharacter() }.start()
    }

    private fun loadTextures() {
        try {
            // Загрузка текстур из ресурсов
            grassTexture = ImageIO.read(File(GRASS_TEXTURE_PATH))
            treeTexture = ImageIO.read(File(TREE_TEXTURE_PATH))
            tree2Texture = ImageIO.read(File(TREE2_TEXTURE_PATH))
            stoneTexture = ImageIO.read(File(STONE_TEXTURE_PATH))
            characterTexture = ImageIO.read(File(CHARACTER_TEXTURE_PATH))
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Не удалось загрузить текстуры!")
        }
    }


    private fun generateWorld() {
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

    private fun placeCharacterInCenter() {
        // Устанавливаем персонажа в центр карты
        val centerTile = tiles.find { it.x == CHARACTER_INITIAL_X && it.y == CHARACTER_INITIAL_Y }
        characterX = centerTile?.x?.toDouble() ?: 0.0
        characterY = centerTile?.y?.toDouble() ?: 0.0
    }

    private fun updateCharacter() {
        // Если персонаж должен двигаться
        if (isMoving && targetX != null && targetY != null) {
            val dx = targetX!! - characterX
            val dy = targetY!! - characterY
            val distance = sqrt(dx * dx + dy * dy)

            if (distance < characterSpeed) {
                // Если персонаж дошёл до точки
                characterX = targetX!!
                characterY = targetY!!
                isMoving = false
            } else {
                // Двигаем персонажа в направлении точки
                characterX += (dx / distance) * characterSpeed
                characterY += (dy / distance) * characterSpeed
            }
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Масштабируем графику
        g2d.scale(scale, scale)

        // Отрисовка мира с учетом смещения камеры
        for (tile in tiles) {
            val screenX = ((tile.x - tile.y) * TILE_SIZE / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((tile.x + tile.y) * TILE_SIZE / 4 + offsetY / scale).toInt()

            when (tile.type) {
                TileType.GRASS -> {
                    g2d.drawImage(
                        grassTexture,
                        screenX - TILE_SIZE / 2,
                        screenY - TILE_SIZE / 4,
                        TILE_SIZE,
                        TILE_SIZE / 2,
                        null
                    )
                }

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

                TileType.TREE -> {
                    g2d.drawImage(
                        grassTexture,
                        screenX - TILE_SIZE / 2,
                        screenY - TILE_SIZE / 4,
                        TILE_SIZE,
                        TILE_SIZE / 2,
                        null
                    )
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
                    g2d.drawImage(
                        grassTexture,
                        screenX - TILE_SIZE / 2,
                        screenY - TILE_SIZE / 4,
                        TILE_SIZE,
                        TILE_SIZE / 2,
                        null
                    )
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
            }
        }

        // Отрисовка персонажа
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
