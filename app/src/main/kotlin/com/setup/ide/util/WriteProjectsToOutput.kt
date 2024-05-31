package com.setup.ide.util

import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

internal class WriteProjectsToOutput : WriteProjects {
  override fun write(to: Path, targets: List<String>, projects: Set<String>): Path {
    // start fresh
    to.deleteIfExists()
    to.createFile()

    to.toFile().bufferedWriter().use { writer ->
      val header = """
        // THIS FILE IS GENERATED AUTOMATICALLY - DO NOT MODIFY
        // targets = ${targets.joinToString(" ")}
      """.trimIndent()
      writer.write(header)
      writer.newLine()
      projects.forEach {
        writer.write("include '${it}'")
        writer.newLine()
      }
    }

    return to
  }
}