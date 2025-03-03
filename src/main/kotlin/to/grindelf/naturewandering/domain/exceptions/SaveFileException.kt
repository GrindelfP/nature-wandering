package to.grindelf.naturewandering.domain.exceptions

class SaveFileException(
    val errorMessage: String
) : Exception(errorMessage)
