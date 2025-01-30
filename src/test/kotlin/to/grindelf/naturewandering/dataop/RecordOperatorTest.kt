package to.grindelf.naturewandering.dataop

import to.grindelf.naturewandering.IsometricWorld
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.test.Test

class RecordOperatorTest {

    private val testWorld = IsometricWorld(
        true,
        "testWorld",
        JFrame(),
        JPanel()
    )

    @Test
    fun testSaveGameTo() {
        RecordOperator.saveGameTo(
            File("testSaveGameTo"),
            testWorld.state(),
            testWorld.playerPosition()
        )
    }
}