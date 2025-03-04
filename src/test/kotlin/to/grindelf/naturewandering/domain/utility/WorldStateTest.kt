package to.grindelf.naturewandering.domain.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import to.grindelf.naturewandering.domain.animals.Bird
import to.grindelf.naturewandering.domain.exceptions.SaveFileException
import to.grindelf.naturewandering.domain.world.Tile
import to.grindelf.naturewandering.domain.world.utility.TileType

class WorldStateTest {

    private val trueWorldState = WorldState(
        2,
        mutableListOf<Tile>(
            Tile(0, 0, TileType.GRASS),
            Tile(0, 1, TileType.TREE),
            Tile(1, 0, TileType.TREE2),
            Tile(1, 1, TileType.STONE)
        ),
        mutableListOf<Bird>(
            Bird(0.0, 0.0, 0.0, 0.0),
            Bird(10.0, 10.0, 10.0, 10.0)
        )
    )

    @Test
    fun `GIVEN string to initialize WorldState WHEN initialized THEN equal to the expected WorldState`() {

        val testStringWorldState = "2|G0;T0;T1;S0;|0.0,0.0,0.0,0.0;10.0,10.0,10.0,10.0;||"

        val testWorldState = WorldState.initFromString(testStringWorldState)

        assertThat(testWorldState).isEqualTo(trueWorldState)
    }

    @Test
    fun `GIVEN string to initialize WorldState without world size WHEN initialized THEN illegal argument exception is thrown`() {

        val testStringWorldState = "|G0;T1;|0.0,0.0,0.0,0.0;10.0,10.0,10.0,10.0;||"

        assertThrows <SaveFileException>  {
            WorldState.initFromString(testStringWorldState)
        }
    }

    @Test
    fun `GIVEN string to initialize WorldState with tile size sqrt not matching world size WHEN initialized THEN illegal argument exception is thrown`() {

        val testStringWorldState = "1|G0;T1;|0.0,0.0,0.0,0.0;10.0,10.0,10.0,10.0;||"

        assertThrows <SaveFileException>  {
            WorldState.initFromString(testStringWorldState)
        }
    }

    @Test
    fun `GIVEN string to initialize WorldState without birds WHEN initialized THEN illegal argument exception is thrown`() {

        val testStringWorldState = "2|G0;T1;|||"

        assertThrows <SaveFileException>  {
            WorldState.initFromString(testStringWorldState)
        }
    }

    @Test
    fun `GIVEN string with wrong structure (to much parts) WHEN initialized THEN illegal argument exception is thrown`() {

        val testStringWorldState = "2|G0;T1;||||||"

        assertThrows <SaveFileException>  {
            WorldState.initFromString(testStringWorldState)
        }
    }

    @Test
    fun `GIVEN string with wrong structure (to little parts) WHEN initialized THEN illegal argument exception is thrown`() {

        val testStringWorldState = "|"

        assertThrows <SaveFileException>  {
            WorldState.initFromString(testStringWorldState)
        }
    }
}