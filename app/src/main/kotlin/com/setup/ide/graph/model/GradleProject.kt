package com.setup.ide.graph.model

import java.nio.file.Path
import kotlin.io.path.exists

/** A wrapper around a gradle project definition **/
data class GradleProject(
  val path: Path, // Full path for this gradle project
  val gradlePath: String // Gradle path is : delimited, e.g. :app:common
) {
  fun containsValidBuildFile(): Boolean =
    path.exists() && path.resolve("build.gradle").exists()
}