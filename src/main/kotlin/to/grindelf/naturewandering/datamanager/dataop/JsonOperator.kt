package to.grindelf.naturewandering.datamanager.dataop

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import to.grindelf.naturewandering.domain.world.Tile
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object JsonOperator {

    private val mapper = jacksonObjectMapper()

    fun saveWorldToFile(world: List<Tile>, outputStream: OutputStream?) {
        if (outputStream != null) mapper.writeValue(outputStream, world)
        else throw IllegalArgumentException("The output stream is null!!")
    }

    fun loadWorldFromFile(inputStream: InputStream?): List<Tile> {
        return if (inputStream != null) mapper.readValue(inputStream, Array<Tile>::class.java).toList()
        else throw IllegalArgumentException("The input stream is null!!")
    }

    fun saveWorldToFile(world: List<Tile>, filepath: String) {
        mapper.writeValue(File(filepath), world)
    }

    fun loadWorldFromFile(filepath: String): List<Tile> =
        mapper.readValue(File(filepath), Array<Tile>::class.java).toList()

    fun saveWorldToFile(world: List<Tile>, file: File) {
        mapper.writeValue(file, world)
    }

    fun loadWorldFromFile(file: File): List<Tile> =
        mapper.readValue(file, Array<Tile>::class.java).toList()

}