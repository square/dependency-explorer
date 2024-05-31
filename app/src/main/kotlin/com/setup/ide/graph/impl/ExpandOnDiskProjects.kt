package com.setup.ide.graph.impl

import com.setup.ide.graph.ExpandProjects
import com.setup.ide.graph.model.GradleProject
import com.setup.ide.util.listSubdirectories
import com.setup.ide.util.toGradlePath
import org.slf4j.Logger
import java.lang.RuntimeException
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue
import kotlin.io.path.exists

/**
 * Expands projects in a repository using breadth-first search.
 * Collects nodes with valid build files (build.gradle).
 * Kotlin build scripts are not yet supported.
 */
internal class ExpandOnDiskProjects(
  private val logger: Logger,
  private val repoRoot: Path
) : ExpandProjects {
  private val allTargets = mutableSetOf<GradleProject>()

  /**
   * Expands modules from input projects and returns all expanded projects.
   *
   * @param input Set of projects to potentially expand.
   * @param repoRoot Root path of the repository to explore.
   * @return Set of all expanded projects from the input.
   */
  override fun expand(input: Set<GradleProject>): Set<GradleProject> {
    input.forEach { project -> bfs(project).forEach { allTargets.add(it) } }
    return allTargets
  }

  private fun bfs(input: GradleProject): Set<GradleProject> {
    if (!input.path.exists()) {
      throw IllegalArgumentException("Invalid directory $input")
    }

    if (input.containsValidBuildFile()) {
      return setOf(input)
    }

    val expandedResults = mutableSetOf<GradleProject>()
    val queue: Queue<GradleProject> = LinkedList()
    queue.add(input)

    while (queue.isNotEmpty()) {
      val nextGradleProject = queue.poll()
      if (nextGradleProject.containsValidBuildFile()) {
        expandedResults.add(nextGradleProject)
      }

      val subdirectories =
        nextGradleProject.path
          .listSubdirectories()
          .filter { subdir -> subdir != BUILD_DIR_NAME && subdir != SRC_DIR_NAME }
          .map { "${nextGradleProject.gradlePath}:$it" }
          .map { GradleProject(path = it.toGradlePath(repoRoot), gradlePath = it) }

      queue.addAll(subdirectories)
    }

    // If an input was invalid we just don't continue - sucks but better than a half right result.
    if (expandedResults.isEmpty()) {
      throw RuntimeException("${input.gradlePath} does not resolve to any known projects")
    }

    return expandedResults
  }

  companion object {
    const val BUILD_DIR_NAME = "build"
    const val SRC_DIR_NAME = "src"
  }
}