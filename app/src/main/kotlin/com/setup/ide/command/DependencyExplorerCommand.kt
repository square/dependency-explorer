package com.setup.ide.command

import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand

@Command(
  name = "explore",
  mixinStandardHelpOptions = true,
  version = ["1.0"],
  description = ["Find modules to sync with for your desired targets"],
  subcommands = [
    GradleModulesCommand::class,
    HelpCommand::class
  ]
)
class DependencyExplorerCommand