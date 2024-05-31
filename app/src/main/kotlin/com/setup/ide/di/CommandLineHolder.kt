package com.setup.ide.di

import com.setup.ide.command.DependencyExplorerCommand
import java.nio.file.Path

internal class CommandLineHolder(
  val command: DependencyExplorerCommand,
  val converterFactory: PicoFactory
) {
  companion object {
    fun create(root: Path): CommandLineHolder =
      CommandLineHolder(
        command = DependencyExplorerCommand(),
        converterFactory = PicoFactory(root)
      )
  }
}