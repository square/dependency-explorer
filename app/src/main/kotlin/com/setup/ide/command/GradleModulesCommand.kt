package com.setup.ide.command

import com.setup.ide.graph.ExpandProjects
import com.setup.ide.graph.FindDependencies
import com.setup.ide.graph.model.GradleProject
import com.setup.ide.util.WriteProjects
import com.setup.ide.util.toGradlePath
import com.setup.ide.util.toNormalizedGradleName
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

/**
 * [GradleModulesCommand] CLI is used to import the requested targets dependencies
 * by collecting all transitive found dependencies (internal only!!) found in
 * the build files of the projects.
 *
 * Use case: In large mono-repositories that have many modules that the IDE would sync to,
 * we can select a subset of those to be considered by our settings.gradle file instead.
 * This becomes especially useful as a feature developer who many not need to build the entire
 * repository to test iterative changes to their work stream.
 *
 * Important to note that this has certain limitations:
 * 1. We consider build files that have statically declared dependencies (toml mapping is not valid)
 * 2. Dependencies must be declared using a project declaration, custom build logic is not respected
 *
 * USAGE:
 *      import project:one project:two ...
 */
@Command(
  showAtFileInUsageHelp = true,
  name = "gradle-projects",
  version = ["1.0"],
  description = ["Import gradle projects for targets to an output file."],
  subcommands = [
    HelpCommand::class
  ]
)
class GradleModulesCommand(
  private val expandProjects: ExpandProjects,
  private val findDependencies: FindDependencies,
  private val writeProjects: WriteProjects,
  private val repo: Path
) : Callable<Path?> {

  @Option(
    names = ["-o", "--output"],
    required = true,
    defaultValue = "settings_modules_override.gradle",
    description = [
      "Absolute path to where module overrides will output."
    ]
  )
  lateinit var output: String

  @Parameters(
    description = ["The gradle projects to collect dependencies for. (REQUIRED)"],
  )
  var inputProjects: List<String> = emptyList()

  override fun call(): Path? {
    if (shouldEarlyExit()) return null

    val gradleProjects = inputProjects
      .map { it.toNormalizedGradleName() }
      .map { it.convertToGradleProject() }
      .toSet()

    println("Collecting module dependencies for inputs " +
      gradleProjects.joinToString(", ") { it.gradlePath })

    val expandedProjects = expandProjects.expand(gradleProjects)
    val allDependencies = findDependencies.getAllTransitiveDependencies(expandedProjects)

    println("Importing ${allDependencies.count()} total modules")

    val output = writeProjects.write(
      to = repo.resolve(output),
      targets = inputProjects,
      projects = allDependencies.map { it.gradlePath }.toSet()
    )

    println("Wrote dependencies to ${output.absolutePathString()}")
    return output
  }

  /**
   * Conditions if met the program should not continue
   *
   * 1.   If the input repository is not defined properly
   * 2.   If the gradle projects to find dependencies for are not passed in
   */
  private fun shouldEarlyExit(): Boolean {
    if (!repo.exists()) {
      println("-r=${repo.absolutePathString()} is not a valid directory. Exiting")
      return true
    }

    if (inputProjects.isEmpty()) {
      println(
        "You must enter gradle projects to find dependencies for." +
          "A gradle project is colon delimited (e.g. `:common:lib`"
      )
      return true
    }
    return false
  }

  private fun String.convertToGradleProject(): GradleProject {
    return GradleProject(
      gradlePath = this,
      path = this.toGradlePath(root = repo)
    )
  }
}