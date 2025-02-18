package to.grindelf.naturewandering.domain.utility

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class PositionTest {

    @Test
    fun `GIVEN string to initialize Player Position WHEN initialized THEN equal to the expected Position`() {

        val truePosition = Position(0.0, 10.0, null, 30.30)
        val testStringPosition = "0.0,10.0,null,30.30"
        val testPosition = Position(testStringPosition)

        assertThat(testPosition).isEqualTo(truePosition)
    }

}