package com.setup.ide.graph.model

/**
 * Data representing what was found in the build file (build.gradle OR build.gradle.kts)
 */
data class BuildInfo(
  val internalDependencies: Set<GradleProject>
)
