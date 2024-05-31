package com.setup.ide

import com.setup.ide.di.CommandLineHolder
import picocli.CommandLine
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.system.measureTimeMillis

private const val REPO_HOME_SYSTEM_ENV = "REPO_HOME"
fun main(args: Array<String>) {
  val root = System.getenv(REPO_HOME_SYSTEM_ENV)
  if (root.isNullOrEmpty()) {
    throw IllegalArgumentException(
      """
        You must set a $REPO_HOME_SYSTEM_ENV=/path/to/repo in order to run this tool.
      """.trimIndent()
    )
  }

  val rootPath = Paths.get(root)
  if (!Paths.get(root).exists()) {
    throw IllegalArgumentException(
      """
        $rootPath is not a valid path.
      """.trimIndent()
    )
  }

  val time = measureTimeMillis {
    val holder = CommandLineHolder.create(rootPath)
    val cli = CommandLine(holder.command, holder.converterFactory)

    cli.isCaseInsensitiveEnumValuesAllowed = true
    cli.execute(*args)
  }

  println("Executed in: $time ms")
}