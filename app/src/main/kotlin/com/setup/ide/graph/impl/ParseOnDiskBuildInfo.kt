package com.setup.ide.graph.impl

import com.setup.ide.graph.ParseBuildInfo
import com.setup.ide.graph.model.BuildInfo
import com.setup.ide.graph.model.GradleProject
import com.setup.ide.util.toGradlePath
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * Parses the build file to pull out [BuildInfo]. This uses a REGEX to do the
 * static parsing for internal dependencies.
 */
class ParseOnDiskBuildInfo(
  private val root: Path
) : ParseBuildInfo {
  override fun parse(buildFile: Path): BuildInfo {
    val text = buildFile.resolve("build.gradle").readText()
    val internalDependencies = PROJECT_REFERENCE_REGEX
      .findAll(text)
      .map { GradleProject(
        path = it.groupValues[1].toGradlePath(root = root),
        gradlePath = it.groupValues[1]
      ) }
      .toSet()

    return BuildInfo(
      internalDependencies = internalDependencies
    )
  }

  companion object {
    private val PROJECT_REFERENCE_REGEX = Regex("\\bproject\\s*\\(?['\"](.+?)['\"]\\)?")
  }
}
