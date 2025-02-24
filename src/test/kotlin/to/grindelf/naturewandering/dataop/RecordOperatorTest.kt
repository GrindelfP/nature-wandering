package to.grindelf.naturewandering.dataop

import org.assertj.core.api.Assertions.assertThat
import to.grindelf.naturewandering.IsometricWorld
import to.grindelf.naturewandering.IsometricWorldConstants
import to.grindelf.naturewandering.datamanager.dataop.RecordOperator
import to.grindelf.naturewandering.domain.maincharacter.MainCharacter
import to.grindelf.naturewandering.domain.utility.Inventory
import to.grindelf.naturewandering.domain.utility.MainCharacterInventory
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
        JPanel(),
        1010,
        MainCharacter(
            Position(10.0, 10.0, null, null),
            false,
            IsometricWorldConstants.CHARACTER_SPEED_DEFAULT,
            IsometricWorldConstants.CHARACTER_HEALTH_MAX_DEFAULT,
            IsometricWorldConstants.CHARACTER_LEVEL_DEFAULT,
            MainCharacterInventory("12,13,14")
        ),

    )

    @Test
    fun testSaveGameTo() {
        RecordOperator.saveGameTo(
            File("testSaveGameTo"),
            testWorld.getWorldState(),
            testWorld.mainCharacter
        )
    }

    @Test
    fun testReadWorldFromFile() {

        // TODO: Fix the bug (somehow the save file and the world are not the same!

        val gameSave = RecordOperator.loadGameFrom(File("testSaveGameTo"))

        val worldState = gameSave.first
        val mainCharacter = gameSave.second

        assertThat(worldState).isEqualTo(testWorld.getWorldState())
        assertThat(mainCharacter).isEqualTo(testWorld.mainCharacter)

    }
}