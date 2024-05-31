package com.setup.ide.graph.impl

import com.setup.ide.graph.FindDependencies
import com.setup.ide.graph.ParseBuildInfo
import com.setup.ide.graph.model.GradleProject
import org.slf4j.Logger
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue

class FindOnDiskDependencies(
  private val logger: Logger,
  private val parseBuildInfo: ParseBuildInfo,
  private val root: Path
) : FindDependencies {
  private val allTransitiveDependencies: MutableSet<GradleProject> = mutableSetOf()
  override fun getAllTransitiveDependencies(
    projects: Set<GradleProject>
  ): Set<GradleProject> {
    for (project in projects) {
      val dependencies = bfs(project)
      allTransitiveDependencies.addAll(dependencies)
    }
    return allTransitiveDependencies
  }

  private fun bfs(
    project: GradleProject
  ): Set<GradleProject> {
    val projectDependencies: MutableSet<GradleProject> = mutableSetOf()
    val projectMap: HashMap<GradleProject, Set<GradleProject>> = HashMap()
    val queue: Queue<GradleProject> = LinkedList<GradleProject>().apply { add(project) }

    while (queue.isNotEmpty()) {
      val currentProjectToExplore = queue.poll()
      if (!projectMap.containsKey(currentProjectToExplore)) {
        val currentProjectDependencies =
          getProjectDependencies(currentProjectToExplore)
        projectDependencies.addAll(currentProjectDependencies)
        queue.addAll(currentProjectDependencies)
        projectMap[currentProjectToExplore] = currentProjectDependencies
      }
    }
    return projectDependencies
  }

  private fun getProjectDependencies(project: GradleProject): Set<GradleProject> {
    return parseBuildInfo.parse(project.path).internalDependencies
  }
}