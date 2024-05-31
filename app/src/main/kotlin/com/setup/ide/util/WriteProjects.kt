package com.setup.ide.util

import java.nio.file.Path

interface WriteProjects {
  fun write(to: Path, targets: List<String>, projects: Set<String>): Path
}