package phonon.xc.util

import org.tomlj.TomlParseError
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

fun listTomlFiles(directory: File): List<File> {
    if (!directory.exists() || !directory.isDirectory) return emptyList()
    return directory.listFiles { f -> f.extension == "toml" }?.toList() ?: emptyList()
}

fun logTomlError(logger: Logger, path: Path, error: TomlParseError) {
    logger.severe("Failed to parse TOML file: $path")
    logger.severe("Error at line ${error.position.line}, column ${error.position.column}: ${error.message}")
}
