package to.grindelf.renderengine

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.random.Random

data class Tile(val x: Int, val y: Int, val type: TileType)

enum class TileType { GRASS, TREE }

class IsometricWorld : JPanel(), KeyListener, MouseWheelListener {

    private val tiles = mutableListOf<Tile>()
    private val tileSize = 64  // Размер одного тайла (размер текстуры)
    private lateinit var grassTexture: Image
    private lateinit var treeTexture: Image

    // Смещение "камеры"
    private var offsetX = 0
    private var offsetY = 100  // Сдвиг мира вниз

    // Масштаб
    private var scale = 1.0  // 1.0 - оригинальный размер, > 1.0 - увеличение, < 1.0 - уменьшение

    init {
        // Загрузка текстур
        loadTextures()

        // Генерация карты
        generateWorld()

        // Добавляем обработчик клавиш
        addKeyListener(this)
        addMouseWheelListener(this)
        isFocusable = true
    }

    private fun loadTextures() {
        try {
            // Загрузка текстур из ресурсов
            grassTexture = ImageIO.read(File("src/main/resources/assets/grass.png"))
            treeTexture = ImageIO.read(File("src/main/resources/assets/tree.png"))
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Не удалось загрузить текстуры!")
        }
    }

    private fun generateWorld() {
        // Создание простого мира 20x20
        for (x in 0 until 20) {
            for (y in 0 until 20) {
                // Случайное размещение деревьев
                val tileType = if (Random.nextFloat() < 0.2) TileType.TREE else TileType.GRASS
                tiles.add(Tile(x, y, tileType))
            }
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
                    // Отрисовка травы как "лежащей" текстуры
                    g2d.drawImage(
                        grassTexture,
                        screenX - tileSize / 2,
                        screenY - tileSize / 4,
                        tileSize,
                        tileSize / 2,
                        null
                    )
                }
                TileType.TREE -> {
                    // Отрисовка травы под деревом
                    g2d.drawImage(
                        grassTexture,
                        screenX - tileSize / 2,
                        screenY - tileSize / 4,
                        tileSize,
                        tileSize / 2,
                        null
                    )

                    // Отрисовка дерева как "стоящей" текстуры
                    val treeHeight = tileSize
                    val treeWidth = tileSize / 2
                    g2d.drawImage(
                        treeTexture,
                        screenX - treeWidth / 2,
                        screenY - treeHeight,
                        treeWidth,
                        treeHeight,
                        null
                    )
                }
            }
        }
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

    // Обработка прокрутки колесика мыши
    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        val notches = e?.wheelRotation
        val zoomFactor = 0.1  // Шаг изменения масштаба

        // Уменьшаем масштаб, если крутим вниз, увеличиваем - если вверх
        scale = (scale - (notches?.times(zoomFactor) ?: 0.0)).coerceIn(0.5, 2.0)

        repaint()  // Перерисовка после изменения масштаба
    }
}
