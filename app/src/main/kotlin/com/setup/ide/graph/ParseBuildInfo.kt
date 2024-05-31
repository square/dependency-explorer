package com.setup.ide.graph

import com.setup.ide.graph.model.BuildInfo
import java.nio.file.Path

interface ParseBuildInfo {
  fun parse(buildFile: Path): BuildInfo
}