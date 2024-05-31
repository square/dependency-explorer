package com.setup.ide.util

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

/**
 * Given an input string like
 *    api/common -> :api:common
 *    /api/common -> :api:common
 *    :api:common -> :api:common
 */
fun String.toNormalizedGradleName(): String {
  return ":" + replace('/', ':')
    .removePrefix(":")
    .removeSuffix(":")
}

/**
 * Given a normalized gradle name like :api:common
 * Return the valid path to the build file from the root
 *
 * Such as:     /User/someuser/repo/api/common
 */
fun String.toGradlePath(root: Path): Path =
  root.resolve(this.removePrefix(":").replace(':', '/'))

/**
 * Collect a list of subdirectories from a given [Path] as a List<String>
 */
fun Path.listSubdirectories(): List<String> {
  return Files.list(this)
    .filter { Files.isDirectory(it) }
    .map { it.fileName.toString() }
    .toList()
}