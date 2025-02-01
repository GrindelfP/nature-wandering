package to.grindelf.naturewandering.dataop

import to.grindelf.naturewandering.IsometricWorld
import to.grindelf.naturewandering.datamanager.dataop.RecordOperator
import to.grindelf.naturewandering.domain.utility.Position
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
            Position(11.0, 11.0, null, null)
        )
    }
}