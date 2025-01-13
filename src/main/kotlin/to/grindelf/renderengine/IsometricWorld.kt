package to.grindelf.renderengine

import java.awt.Graphics
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.random.Random

data class Tile(val x: Int, val y: Int, val type: TileType)

enum class TileType { GRASS, TREE }

class IsometricWorld : JPanel(), KeyListener {

    private val tiles = mutableListOf<Tile>()
    private val tileSize = 64  // Размер одного тайла (размер текстуры)
    private lateinit var grassTexture: Image
    private lateinit var treeTexture: Image

    // Смещение "камеры"
    private var offsetX = 0
    private var offsetY = 100

    init {
        // Загрузка текстур
        loadTextures()

        // Генерация карты
        generateWorld()

        // Добавляем обработчик клавиш
        addKeyListener(this)
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

        // Отрисовка мира с учетом смещения камеры
        for (tile in tiles) {
            val screenX = (tile.x - tile.y) * tileSize / 2 + width / 2 + offsetX
            val screenY = (tile.x + tile.y) * tileSize / 4 + offsetY

            when (tile.type) {
                TileType.GRASS -> {
                    // Отрисовка травы как "лежащей" текстуры
                    g.drawImage(grassTexture, screenX - tileSize / 2, screenY - tileSize / 4, tileSize, tileSize / 2, null)
                }
                TileType.TREE -> {
                    // Отрисовка травы под деревом
                    g.drawImage(grassTexture, screenX - tileSize / 2, screenY - tileSize / 4, tileSize, tileSize / 2, null)

                    // Отрисовка дерева как "стоящей" текстуры
                    val treeHeight = tileSize
                    val treeWidth = tileSize / 2
                    g.drawImage(treeTexture, screenX - treeWidth / 2, screenY - treeHeight, treeWidth, treeHeight, null)
                }
            }
        }
    }

    // Обработка нажатий клавиш
    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_W -> offsetY += 20  // Движение вверх
            KeyEvent.VK_S -> offsetY -= 20  // Движение вниз
            KeyEvent.VK_A -> offsetX += 20  // Движение влево
            KeyEvent.VK_D -> offsetX -= 20 // Движение вправо
        }
        repaint()  // Перерисовываем мир после изменения смещения
    }

    override fun keyReleased(e: KeyEvent?) {}
    override fun keyTyped(e: KeyEvent?) {}

}
