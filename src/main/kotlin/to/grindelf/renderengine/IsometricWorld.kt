package to.grindelf.renderengine

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
    private val tileSize = 64  // Размер одного тайла (размер текстуры)
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
            grassTexture = ImageIO.read(File("src/main/resources/assets/grass.png"))
            treeTexture = ImageIO.read(File("src/main/resources/assets/tree.png"))
            tree2Texture = ImageIO.read(File("src/main/resources/assets/tree2.png"))
            stoneTexture = ImageIO.read(File("src/main/resources/assets/stone.png"))
            characterTexture = ImageIO.read(File("src/main/resources/assets/character.png"))
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Не удалось загрузить текстуры!")
        }
    }


    private fun generateWorld() {
        for (x in 0 until 80) {
            for (y in 0 until 80) {
                val randomValue = Random.nextFloat()
                val tileType = when {
                    randomValue < 0.1 -> TileType.STONE  // Камень (10% вероятность)
                    randomValue < 0.3 -> TileType.TREE   // Первый тип дерева (20% вероятность)
                    randomValue < 0.5 -> TileType.TREE2  // Второй тип дерева (20% вероятность)
                    else -> TileType.GRASS              // Трава (50% вероятность)
                }
                tiles.add(Tile(x, y, tileType))
            }
        }
    }

    private fun placeCharacterInCenter() {
        // Устанавливаем персонажа в центр карты
        val centerTile = tiles.find { it.x == 10 && it.y == 10 }
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
            val screenX = ((tile.x - tile.y) * tileSize / 2 + width / 2 / scale + offsetX / scale).toInt()
            val screenY = ((tile.x + tile.y) * tileSize / 4 + offsetY / scale).toInt()

            when (tile.type) {
                TileType.GRASS -> {
                    g2d.drawImage(
                        grassTexture,
                        screenX - tileSize / 2,
                        screenY - tileSize / 4,
                        tileSize,
                        tileSize / 2,
                        null
                    )
                }

                TileType.STONE -> {
                    g2d.drawImage(
                        stoneTexture,
                        screenX - tileSize / 4,
                        screenY - tileSize / 4,
                        tileSize / 2,
                        tileSize / 2,
                        null
                    )
                }

                TileType.TREE -> {
                    g2d.drawImage(
                        grassTexture,
                        screenX - tileSize / 2,
                        screenY - tileSize / 4,
                        tileSize,
                        tileSize / 2,
                        null
                    )
                    val treeHeight = tileSize
                    val treeWidth = tileSize / 2
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
                        screenX - tileSize / 2,
                        screenY - tileSize / 4,
                        tileSize,
                        tileSize / 2,
                        null
                    )
                    val treeHeight = tileSize
                    val treeWidth = tileSize / 2
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
        val characterScreenX = ((characterX - characterY) * tileSize / 2 + width / 2 / scale + offsetX / scale).toInt()
        val characterScreenY = ((characterX + characterY) * tileSize / 4 + offsetY / scale).toInt()
        g2d.drawImage(
            characterTexture,
            characterScreenX - tileSize / 6,
            characterScreenY - tileSize / 3,
            tileSize / 3,
            tileSize / 3,
            null
        )


        // --- Добавляем текст с инструкциями ---
//            g2d.scale(1 / scale, 1 / scale) // Отключаем масштабирование для текста
//            g2d.drawString("Управление камерой:", 10, 20)
//            g2d.drawString("W/S/A/D - двигать камеру", 10, 40)
//            g2d.drawString("Колесо мыши - зумировать", 10, 60)
//            g2d.drawString("Управление персонажем:", 10, 80)
//            g2d.drawString("ЛКМ - отправить персонажа в точку", 10, 100)

    }


    // Обработка нажатий клавиш
    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_W -> offsetY += 20  // Движение вверх
            KeyEvent.VK_S -> offsetY -= 20  // Движение вниз
            KeyEvent.VK_A -> offsetX += 20  // Движение влево
            KeyEvent.VK_D -> offsetX -= 20  // Движение вправо
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
        val worldX = ((clickX / (tileSize / 2)) + (clickY / (tileSize / 4))) / 2
        val worldY = ((clickY / (tileSize / 4)) - (clickX / (tileSize / 2))) / 2

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
        val zoomFactor = 0.1  // Шаг изменения масштаба

        // Уменьшаем масштаб, если крутим вниз, увеличиваем - если вверх
        scale = (scale - (notches?.times(zoomFactor) ?: 0.0)).coerceIn(1.0, 4.0)

        repaint()  // Перерисовка после изменения масштаба
    }
}