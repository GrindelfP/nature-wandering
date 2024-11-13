package to.grindelf.renderengine

import org.joml.Matrix4f
import org.joml.Vector3f
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.max

class Camera {
    val position = Vector3f(0f, 0f, -5f)
    val projectionMatrix = Matrix4f().setPerspective(Math.toRadians(70.0).toFloat(), 800f / 600f, 0.1f, 100f)

    fun getViewMatrix(): Matrix4f {
        return Matrix4f().translate(-position.x, -position.y, -position.z)
    }
}

data class Light(val direction: Vector3f, val color: Color)

class Mesh(val vertices: List<Vector3f>, val faces: List<List<Int>>)

class RenderPanel : JPanel() {
    private val camera = Camera()
    private val cube = createCube()
    private val light = Light(Vector3f(-1f, -1f, -1f).normalize(), Color.WHITE)

    private var rotationX = 0f
    private var rotationY = 0f

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                requestFocus()
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                rotationX += e.y.toFloat() * 0.0001f
                rotationY += e.x.toFloat() * 0.0001f
                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val transform = Matrix4f()
            .mul(camera.projectionMatrix)
            .mul(camera.getViewMatrix())
            .rotateX(rotationX)
            .rotateY(rotationY)

        val projectedVertices = cube.vertices.map { vertex ->
            val transformed = Vector3f()
            transform.transformPosition(vertex, transformed)

            // Проверяем значение Z для корректной перспективной проекции
            val screenX = (transformed.x / transformed.z) * width / 2 + width / 2
            val screenY = -(transformed.y / transformed.z) * height / 2 + height / 2
            Vector3f(screenX, screenY, transformed.z)
        }

        cube.faces.forEach { face ->
            val faceNormal = calculateFaceNormal(
                cube.vertices[face[0]],
                cube.vertices[face[1]],
                cube.vertices[face[2]]
            )
            val lightIntensity = max(0f, faceNormal.dot(light.direction))

            val faceColor = Color(
                (light.color.red * lightIntensity).toInt().coerceAtMost(255),
                (light.color.green * lightIntensity).toInt().coerceAtMost(255),
                (light.color.blue * lightIntensity).toInt().coerceAtMost(255)
            )

            g.color = faceColor
            val polygon = face.map { projectedVertices[it] }.map { p -> intArrayOf(p.x.toInt(), p.y.toInt()) }
            val xPoints = polygon.map { it[0] }.toIntArray()
            val yPoints = polygon.map { it[1] }.toIntArray()
            g.fillPolygon(xPoints, yPoints, face.size)
        }
    }

    private fun calculateFaceNormal(v0: Vector3f, v1: Vector3f, v2: Vector3f): Vector3f {
        val edge1 = Vector3f(v1).sub(v0)
        val edge2 = Vector3f(v2).sub(v0)
        return edge1.cross(edge2).normalize()
    }
}

fun createCube(): Mesh {
    val vertices = listOf(
        Vector3f(-1f, -1f, -1f), Vector3f(1f, -1f, -1f),
        Vector3f(1f, 1f, -1f), Vector3f(-1f, 1f, -1f),
        Vector3f(-1f, -1f, 1f), Vector3f(1f, -1f, 1f),
        Vector3f(1f, 1f, 1f), Vector3f(-1f, 1f, 1f)
    )
    val faces = listOf(
        listOf(0, 1, 2, 3), listOf(4, 5, 6, 7),
        listOf(0, 3, 7, 4), listOf(1, 2, 6, 5),
        listOf(0, 1, 5, 4), listOf(3, 2, 6, 7)
    )
    return Mesh(vertices, faces)
}

fun main() {
    JFrame().apply {
        title = "3D Renderer with Lighting and Rotation"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(800, 600)
        add(RenderPanel())
        isVisible = true
    }
}
