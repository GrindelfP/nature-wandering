package to.grindelf.naturewandering

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object JsonOperator {

    private val mapper = jacksonObjectMapper()

    fun saveWorldToFile(world: List<Tile>, filename: String) {
        mapper.writeValue(File(filename), world)
    }

    fun loadWorldFromFile(filename: String): List<Tile> =
        mapper.readValue(File(filename), Array<Tile>::class.java).toList()

}
