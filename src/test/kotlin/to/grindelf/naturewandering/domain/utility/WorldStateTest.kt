package to.grindelf.naturewandering.domain.utility

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import to.grindelf.naturewandering.domain.animals.Bird
import to.grindelf.naturewandering.domain.world.Tile
import to.grindelf.naturewandering.domain.world.utility.TileType

class WorldStateTest {

    @Test
    fun `GIVEN string to initialize WorldState WHEN initialized THEN equal to the expected WorldState`() {

        val trueWorldState = WorldState(
            mutableListOf<Tile>(
                Tile(0, 0, TileType.GRASS),
                Tile(1, 0, TileType.TREE2)
            ),
            mutableListOf<Bird>(
                Bird(0.0, 0.0, 0.0, 0.0),
                Bird(10.0, 10.0, 10.0, 10.0)
            )
        )
        val testStringWorldState = "G1;T2;|0.0,0.0,0.0,0.0;10.0,10.0,10.0,10.0;|"

        val testWorldState = WorldState(testStringWorldState)

        assertThat(testWorldState).isEqualTo(trueWorldState)
    }

}