package com.setup.ide.converter

import picocli.CommandLine.ITypeConverter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Converts to a [Path] from [String]
 */
class PathConverter : ITypeConverter<Path> {
  override fun convert(value: String?): Path {
    if (value == null) {
      throw NullPointerException("You must set a valid path to a repository!")
    }

    return Paths.get(value)
  }
}