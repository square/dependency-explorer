package com.setup.ide.di

import com.setup.ide.command.GradleModulesCommand
import com.setup.ide.di.GenericKoinModule.Companion.genericKoinApplication
import com.setup.ide.di.GenericKoinModule.Companion.genericModule
import com.setup.ide.graph.ExpandProjects
import com.setup.ide.graph.FindDependencies
import com.setup.ide.graph.ParseBuildInfo
import com.setup.ide.graph.impl.ExpandOnDiskProjects
import com.setup.ide.graph.impl.FindOnDiskDependencies
import com.setup.ide.graph.impl.ParseOnDiskBuildInfo
import com.setup.ide.util.WriteProjects
import com.setup.ide.util.WriteProjectsToOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import java.nio.file.Path

class PicoFactory(
  private val root: Path
) : IFactory {

  private val commands = genericModule {
    single { GradleModulesCommand(get(), get(), get(), root) }
  }

  private val graph = genericModule {
    // on-disk
    single<ParseBuildInfo> { ParseOnDiskBuildInfo(root) }
    single<ExpandProjects> { ExpandOnDiskProjects(get(), root) }
    single<FindDependencies> { FindOnDiskDependencies(get(), get(), root) }
  }

  private val utils = genericModule {
    single<Logger> { LoggerFactory.getLogger("setup-ide") }
    single<WriteProjects> { WriteProjectsToOutput() }
  }

  private val koin by lazy {
    genericKoinApplication {
      modules(
        commands,
        graph,
        utils
      )
    }
  }

  override fun <K : Any> create(cls: Class<K>): K {
    return koin.getConcreteOrNull(cls.kotlin)
      ?: CommandLine.defaultFactory().create(cls)
  }
}