package com.setup.ide.graph

import com.setup.ide.graph.model.GradleProject
import java.nio.file.Path

interface FindDependencies {
  fun getAllTransitiveDependencies(projects: Set<GradleProject>): Set<GradleProject>
}